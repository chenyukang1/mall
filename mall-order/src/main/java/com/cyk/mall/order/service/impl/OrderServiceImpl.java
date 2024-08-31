package com.cyk.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyk.mall.common.domain.constant.Constants;
import com.cyk.mall.common.domain.req.LockStockReq;
import com.cyk.mall.common.mq.handler.ITransactionMsgHandler;
import com.cyk.mall.common.support.ids.factory.IdGeneratorFactory;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.Query;
import com.cyk.mall.common.utils.R;
import com.cyk.mall.order.dao.MQTaskDao;
import com.cyk.mall.order.dao.OrderDao;
import com.cyk.mall.order.domain.aggregate.SubmitOrderAggregate;
import com.cyk.mall.order.domain.po.MQTaskEntity;
import com.cyk.mall.order.domain.po.OrderEntity;
import com.cyk.mall.order.domain.req.SubmitOrderReq;
import com.cyk.mall.order.feign.StockFeignService;
import com.cyk.mall.order.mq.event.DelayCloseOrderEvent;
import com.cyk.mall.order.mq.event.SubmitOrderSuccessEvent;
import com.cyk.mall.order.mq.producer.DelayCloseOrderProducer;
import com.cyk.mall.order.mq.producer.SubmitOrderSuccessProducer;
import com.cyk.mall.order.mq.transaction.handler.SubmitOrderTransactionMsgHandler;
import com.cyk.mall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

@Service("orderService")
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private MQTaskDao mqTaskDao;
    @Resource
    private StockFeignService stockFeignService;
    @Resource
    private IdGeneratorFactory idGeneratorFactory;
    @Resource
    private DelayCloseOrderProducer delayCloseOrderProducer;
    @Resource
    private SubmitOrderSuccessProducer submitOrderSuccessProducer;
    @Resource
    private SubmitOrderTransactionMsgHandler submitOrderTransactionMsgHandler;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void submitOrder(SubmitOrderReq submitOrderReq) {
        // 1.1、生成订单流水号 TODO 雪花算法
        String orderSn = idGeneratorFactory.get(Constants.IdGeneratorType.UUID_GENERATOR).nextId();
        // 1.2、生成业务仿重ID
        String outBusinessNo = idGeneratorFactory.get(Constants.IdGeneratorType.UUID_GENERATOR).nextId();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(submitOrderReq.getUserId());
        orderEntity.setSku(submitOrderReq.getSku());
        orderEntity.setOrderSn(orderSn);
        orderEntity.setOutBusinessNo(outBusinessNo);
        orderEntity.setMoney(BigDecimal.valueOf(Double.parseDouble(submitOrderReq.getMoney())));
        orderEntity.setCount(submitOrderReq.getCount());
        orderEntity.setStatus(0);

        // 3.1、创建消息task
        DelayCloseOrderEvent delayCloseOrderEvent = new DelayCloseOrderEvent();
        delayCloseOrderEvent.setOutBusinessNo(outBusinessNo);
        delayCloseOrderEvent.setOrderSn(orderSn);

        transactionTemplate.execute(status -> {
            try {
                // 1、创建订单，写db
                save(orderEntity);

                // 2、调用远程锁库存，失败直接回滚
                // 如果这步因为网络原因异常，订单回滚了，但库存扣减成功了怎么办？
                // - 2.1、异步对账
                // - 2.2、库存回滚
                LockStockReq lockStockReq = new LockStockReq();
                lockStockReq.setOrderSn(orderSn);
                lockStockReq.setSku(submitOrderReq.getSku());
                lockStockReq.setLockCount(submitOrderReq.getCount());
                R res = stockFeignService.lockStock(lockStockReq);
                if (res.getCode() != 0) {
                    log.error("扣减库存失败");
                    throw new RuntimeException("扣减库存失败");
                }

                // 3、发送到订单延迟队列，超时关单
                // 3.2、消息task记录写db
                MQTaskEntity mqTaskEntity = new MQTaskEntity();
                mqTaskEntity.setUserId(submitOrderReq.getUserId());
                mqTaskEntity.setTopic(Constants.Topic.DELAY_CLOSE_ORDER_TOPIC);
                mqTaskEntity.setMessageId(outBusinessNo);
                mqTaskEntity.setMessage(delayCloseOrderEvent);
                mqTaskEntity.setState(Constants.MQTaskStatus.CREATE);
                mqTaskDao.insert(mqTaskEntity);

                return 1;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("下单失败", e);
                throw e;
            }
        });

        // 3.3、发消息失败不回滚事务，等待定时任务扫task补偿
        try {
            SendResult sendResult = delayCloseOrderProducer.syncSend(delayCloseOrderEvent);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                log.info("定时关单消息 {} 发送成功", delayCloseOrderEvent);
                MQTaskEntity updateEntity = new MQTaskEntity();
                updateEntity.setState(Constants.MQTaskStatus.COMPLETED);
                QueryWrapper<MQTaskEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("message_id", outBusinessNo);
                mqTaskDao.update(updateEntity, queryWrapper);
            } else {
                log.info("定时关单消息 {} 发送失败", delayCloseOrderEvent);
                MQTaskEntity updateEntity = new MQTaskEntity();
                updateEntity.setState(Constants.MQTaskStatus.FAIL);
                QueryWrapper<MQTaskEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("message_id", outBusinessNo);
                mqTaskDao.update(updateEntity, queryWrapper);
            }
        } catch (Exception e) {
            log.info("定时关单消息 {} 发送失败", delayCloseOrderEvent);
            MQTaskEntity updateEntity = new MQTaskEntity();
            updateEntity.setState(Constants.MQTaskStatus.FAIL);
            QueryWrapper<MQTaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("message_id", outBusinessNo);
            mqTaskDao.update(updateEntity, queryWrapper);
        }
    }

    /**
     * submitOrderV2是submitOrder的拓展，把写订单DB+远程调用扣库存+超时关单mq看作本地事务
     * 发送下单成功事务消息，本地事务成功，才发给下游支付中心，创建预支付
     *
     * @param submitOrderReq submitOrderReq
     */
    @Override
    public void submitOrderV2(SubmitOrderReq submitOrderReq) {
        // 1.1、生成订单流水号 TODO 雪花算法
        String orderSn = idGeneratorFactory.get(Constants.IdGeneratorType.UUID_GENERATOR).nextId();
        // 1.2、生成业务仿重ID
        String outBusinessNo = idGeneratorFactory.get(Constants.IdGeneratorType.UUID_GENERATOR).nextId();

        // 2、下单成功事务消息
        SubmitOrderSuccessEvent submitOrderSuccessEvent = new SubmitOrderSuccessEvent();
        submitOrderSuccessEvent.setAmount(new BigDecimal(submitOrderReq.getMoney()));
        submitOrderSuccessEvent.setOrderId(Long.valueOf(orderSn));
        submitOrderSuccessEvent.setUuid(outBusinessNo);
        submitOrderSuccessEvent.setDescription("商品sku " + submitOrderReq.getSku() + " 对应的商品描述");

        // 3、生成订单聚合对象
        SubmitOrderAggregate submitOrderAggregate = new SubmitOrderAggregate();
        submitOrderAggregate.setSku(submitOrderAggregate.getSku());
        submitOrderAggregate.setUserId(submitOrderReq.getUserId());
        submitOrderAggregate.setCount(submitOrderReq.getCount());
        submitOrderAggregate.setMoney(submitOrderReq.getMoney());
        submitOrderAggregate.setOrderSn(orderSn);
        submitOrderAggregate.setOutBusinessNo(outBusinessNo);

        // 4、发送事务消息
        ITransactionMsgHandler transactionMsgHandler = transactionId ->
                submitOrderTransactionMsgHandler.handle(transactionId, submitOrderAggregate);
        submitOrderSuccessProducer.sendMessageInTransaction(submitOrderSuccessEvent, transactionMsgHandler);
    }

}