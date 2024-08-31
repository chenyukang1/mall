package com.cyk.mall.order.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyk.mall.common.domain.constant.Constants;
import com.cyk.mall.order.dao.MQRecordDao;
import com.cyk.mall.order.dao.MQTaskDao;
import com.cyk.mall.order.dao.OrderDao;
import com.cyk.mall.order.domain.po.MQRecordEntity;
import com.cyk.mall.order.domain.po.MQTaskEntity;
import com.cyk.mall.order.domain.po.OrderEntity;
import com.cyk.mall.order.mq.event.DelayCloseOrderEvent;
import com.cyk.mall.order.mq.event.RollbackStockEvent;
import com.cyk.mall.order.mq.producer.UnlockStockProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.UUID;

/**
 * The class DelayCloseOrderConsumer.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/11
 */
@Component
@RocketMQMessageListener(
        consumerGroup = Constants.ConsumerGroup.DELAY_CLOSE_ORDER_CONSUMER_GROUP,
        topic = Constants.Topic.DELAY_CLOSE_ORDER_TOPIC
)
@Slf4j
public class DelayCloseOrderConsumer implements RocketMQListener<DelayCloseOrderEvent> {

    @Resource
    private OrderDao orderDao;
    @Resource
    private MQRecordDao mqRecordDao;
    @Resource
    private MQTaskDao mqTaskDao;
    @Resource
    UnlockStockProducer unlockStockProducer;

    @Transactional
    @Override
    public void onMessage(DelayCloseOrderEvent delayCloseOrderEvent) {
        log.info("【延迟关闭订单消息】开始消费：{}", JSON.toJSONString(delayCloseOrderEvent));

        // 1、【幂等性】插入本地消息记录表
        MQRecordEntity mqRecordEntity = new MQRecordEntity();
        mqRecordEntity.setTopic(Constants.Topic.DELAY_CLOSE_ORDER_TOPIC);
        mqRecordEntity.setMessageId(delayCloseOrderEvent.getOutBusinessNo());
        mqRecordEntity.setMessage(delayCloseOrderEvent);
        mqRecordDao.insert(mqRecordEntity);

        // 2、超时关单
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<OrderEntity>()
                .eq("order_sn", delayCloseOrderEvent.getOrderSn());
        OrderEntity orderEntity = orderDao.selectOne(queryWrapper);
        if (Objects.equals(orderEntity.getStatus(), Constants.OrderStatus.COMPLETED.getStatus())) {
            log.info("【延迟关闭订单消息】消费完成：{}", JSON.toJSONString(delayCloseOrderEvent));
            return;
        }

        // 2.1、修改订单状态
        OrderEntity updateEntity = new OrderEntity();
        updateEntity.setStatus(Constants.OrderStatus.CANCELED.getStatus());
        orderDao.update(updateEntity, queryWrapper);

        // 2.2、回滚库存消息入库
        RollbackStockEvent rollbackStockEvent = new RollbackStockEvent();
        rollbackStockEvent.setUuid(UUID.randomUUID().toString());
        rollbackStockEvent.setSku(orderEntity.getSku());
        rollbackStockEvent.setCount(orderEntity.getCount());

        MQTaskEntity MQTaskEntity = new MQTaskEntity();
        MQTaskEntity.setUserId(orderEntity.getUserId());
        MQTaskEntity.setTopic(Constants.Topic.UNLOCK_STOCK_TOPIC);
        MQTaskEntity.setMessageId(rollbackStockEvent.getUuid());
        MQTaskEntity.setMessage(rollbackStockEvent);
        MQTaskEntity.setState(Constants.MQTaskStatus.CREATE);
        mqTaskDao.insert(MQTaskEntity);

        // 3、回滚库存，发消息失败不回滚事务，等待定时任务扫task补偿
        try {
            SendResult sendResult = unlockStockProducer.syncSend(rollbackStockEvent);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                log.info("定时关单消息 {} 发送成功", delayCloseOrderEvent);
                MQTaskEntity taskUpdateEntity = new MQTaskEntity();
                taskUpdateEntity.setState(Constants.MQTaskStatus.COMPLETED);
                QueryWrapper<MQTaskEntity> taskQueryWrapper = new QueryWrapper<>();
                taskQueryWrapper.eq("message_id", rollbackStockEvent.getUuid());
                mqTaskDao.update(taskUpdateEntity, taskQueryWrapper);
            } else {
                log.info("定时关单消息 {} 发送失败", delayCloseOrderEvent);
                MQTaskEntity taskUpdateEntity = new MQTaskEntity();
                taskUpdateEntity.setState(Constants.MQTaskStatus.FAIL);
                QueryWrapper<MQTaskEntity> taskQueryWrapper = new QueryWrapper<>();
                taskQueryWrapper.eq("message_id", rollbackStockEvent.getUuid());
                mqTaskDao.update(taskUpdateEntity, taskQueryWrapper);
            }
        } catch (Exception e) {
            log.info("定时关单消息 {} 发送失败", delayCloseOrderEvent);
            MQTaskEntity taskUpdateEntity = new MQTaskEntity();
            taskUpdateEntity.setState(Constants.MQTaskStatus.FAIL);
            QueryWrapper<MQTaskEntity> taskQueryWrapper = new QueryWrapper<>();
            taskQueryWrapper.eq("message_id", rollbackStockEvent.getUuid());
            mqTaskDao.update(taskUpdateEntity, taskQueryWrapper);
        }
    }

}
