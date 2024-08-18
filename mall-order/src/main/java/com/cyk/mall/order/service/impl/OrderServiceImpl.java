package com.cyk.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyk.mall.common.constant.Constants;
import com.cyk.mall.common.req.LockStockReq;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.Query;
import com.cyk.mall.common.utils.R;
import com.cyk.mall.order.dao.OrderDao;
import com.cyk.mall.order.dao.MQTaskDao;
import com.cyk.mall.order.domain.po.MQTaskEntity;
import com.cyk.mall.order.domain.po.OrderEntity;
import com.cyk.mall.order.domain.req.SubmitOrderReq;
import com.cyk.mall.order.domain.to.OrderTo;
import com.cyk.mall.order.feign.StockFeignService;
import com.cyk.mall.order.mq.event.DelayCloseOrderEvent;
import com.cyk.mall.order.mq.producer.DelayCloseOrderProducer;
import com.cyk.mall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

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
    private DelayCloseOrderProducer delayCloseOrderProducer;

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
        String orderSn = String.valueOf(new Random().nextLong());
        // 1.2、生成业务仿重ID TODO 雪花算法
        String outBusinessNo = String.valueOf(new Random().nextLong());

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
                MQTaskEntity MQTaskEntity = new MQTaskEntity();
                MQTaskEntity.setUserId(submitOrderReq.getUserId());
                MQTaskEntity.setTopic(Constants.Topic.DELAY_CLOSE_ORDER_TOPIC);
                MQTaskEntity.setMessageId(outBusinessNo);
                MQTaskEntity.setMessage(delayCloseOrderEvent);
                MQTaskEntity.setState(Constants.MQTaskStatus.CREATE);
                mqTaskDao.insert(MQTaskEntity);

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

    @Override
    public void submitOrderV2(SubmitOrderReq submitOrderReq) {
        // 1.1、生成订单流水号 TODO 雪花算法
        String orderSn = String.valueOf(new Random().nextLong());

        // 1.2、生成业务仿重ID TODO 雪花算法
        String outBusinessNo = String.valueOf(new Random().nextLong());

        // 3.1、创建消息task
        DelayCloseOrderEvent delayCloseOrderEvent = new DelayCloseOrderEvent();
        delayCloseOrderEvent.setOutBusinessNo(outBusinessNo);
        delayCloseOrderEvent.setOrderSn(orderSn);
        // TODO 事务消息
//        delayCloseOrderProducer.sendMessageInTransaction()
    }

    @Transactional(rollbackFor = Exception.class)
//    @Override
    public void closeOrder(OrderTo orderTo) {
        OrderEntity exist = getById(orderTo.getId());
        // 订单未完成，则取消
        if (exist.getStatus() == 0) {
            OrderEntity update = new OrderEntity();
            update.setId(orderTo.getId());
            update.setStatus(3);
            updateById(update);

//            R r = storageFeignService.productInfo(orderTo.getProductId());
//            if (r.getCode() != 0) {
//                // 远程调用失败就回滚db，消息重新入队
//                throw new RuntimeException("调用库存服务失败！");
//            }
//            StockTo stockTo = r.getData("storage", new TypeReference<StockTo>() {});
//            stockTo.setRollback(orderTo.getUsed());
//            // 发消息给回滚库存队列，回滚该订单库存
//            rabbitTemplate.convertAndSend(Exchanges.STOCK_EVENT_EXCHANGE.getName(),
//                    RoutingKey.STOCK_RELEASE_ORDER.getName(), stockTo);
        }
    }

}