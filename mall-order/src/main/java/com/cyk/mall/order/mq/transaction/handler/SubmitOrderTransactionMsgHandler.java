package com.cyk.mall.order.mq.transaction.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyk.mall.common.domain.constant.Constants;
import com.cyk.mall.common.domain.req.LockStockReq;
import com.cyk.mall.common.utils.R;
import com.cyk.mall.order.dao.MQTaskDao;
import com.cyk.mall.order.dao.MQTransactionDao;
import com.cyk.mall.order.dao.OrderDao;
import com.cyk.mall.order.domain.aggregate.SubmitOrderAggregate;
import com.cyk.mall.order.domain.po.MQTaskEntity;
import com.cyk.mall.order.domain.po.MQTransactionEntity;
import com.cyk.mall.order.domain.po.OrderEntity;
import com.cyk.mall.order.feign.StockFeignService;
import com.cyk.mall.order.mq.event.DelayCloseOrderEvent;
import com.cyk.mall.order.mq.producer.DelayCloseOrderProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * The class DelayCloseOrderTransactionMsgHandler.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/19
 */
@Component
@Slf4j
public class SubmitOrderTransactionMsgHandler {

    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private StockFeignService stockFeignService;
    @Resource
    private DelayCloseOrderProducer delayCloseOrderProducer;
    @Resource
    private OrderDao orderDao;
    @Resource
    private MQTaskDao mqTaskDao;
    @Resource
    private MQTransactionDao mqTransactionDao;

    public boolean handle(String transactionId, SubmitOrderAggregate submitOrderAggregate) {
        String orderSn = submitOrderAggregate.getOrderSn();
        String outBusinessNo = submitOrderAggregate.getOutBusinessNo();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(submitOrderAggregate.getUserId());
        orderEntity.setSku(submitOrderAggregate.getSku());
        orderEntity.setOrderSn(orderSn);
        orderEntity.setOutBusinessNo(outBusinessNo);
        orderEntity.setMoney(BigDecimal.valueOf(Double.parseDouble(submitOrderAggregate.getMoney())));
        orderEntity.setCount(submitOrderAggregate.getCount());
        orderEntity.setStatus(0);

        // 3.1、创建消息task
        DelayCloseOrderEvent delayCloseOrderEvent = new DelayCloseOrderEvent();
        delayCloseOrderEvent.setOutBusinessNo(outBusinessNo);
        delayCloseOrderEvent.setOrderSn(orderSn);

        transactionTemplate.execute(status -> {
            try {
                // 1、创建订单，写db
                orderDao.insert(orderEntity);

                // 2、调用远程锁库存，失败直接回滚
                // 如果这步因为网络原因异常，订单回滚了，但库存扣减成功了怎么办？
                // - 2.1、库存对账 + 库存回滚
                LockStockReq lockStockReq = new LockStockReq();
                lockStockReq.setOrderSn(orderSn);
                lockStockReq.setSku(submitOrderAggregate.getSku());
                lockStockReq.setLockCount(submitOrderAggregate.getCount());
                R res = stockFeignService.lockStock(lockStockReq);
                if (res.getCode() != 0) {
                    log.error("扣减库存失败");
                    throw new RuntimeException("扣减库存失败");
                }

                // 3、发送到订单延迟队列，超时关单
                // 3.2、消息task记录写db
                MQTaskEntity MQTaskEntity = new MQTaskEntity();
                MQTaskEntity.setUserId(submitOrderAggregate.getUserId());
                MQTaskEntity.setTopic(Constants.Topic.DELAY_CLOSE_ORDER_TOPIC);
                MQTaskEntity.setMessageId(outBusinessNo);
                MQTaskEntity.setMessage(delayCloseOrderEvent);
                MQTaskEntity.setState(Constants.MQTaskStatus.CREATE);
                mqTaskDao.insert(MQTaskEntity);

                // 4、消息事务记录写DB，便于事务反查
                MQTransactionEntity mqTransactionEntity = new MQTransactionEntity();
                mqTransactionEntity.setTransactionId(transactionId);
                mqTransactionDao.insert(mqTransactionEntity);

                return 1;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("下单失败", e);
                throw new RuntimeException("下单失败");
            }
        });

        // 3.3、发消息失败不影响结果，等待定时任务扫task补偿
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

        return true;
    }
}
