package com.cyk.mall.pay.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.cyk.mall.common.domain.constant.Constants;
import com.cyk.mall.pay.dao.MQRecordDao;
import com.cyk.mall.pay.domain.po.MQRecordEntity;
import com.cyk.mall.pay.mq.event.SubmitOrderSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * The class DelayCloseOrderConsumer.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/11
 */
@Component
@RocketMQMessageListener(
        consumerGroup = Constants.ConsumerGroup.SUBMIT_ORDER_SUCCESS_PAY_CONSUMER_GROUP,
        topic = Constants.Topic.SUBMIT_ORDER_SUCCESS_TOPIC
)
@Slf4j
public class SubmitOrderSuccessConsumer implements RocketMQListener<SubmitOrderSuccessEvent> {

    @Resource
    private MQRecordDao mqRecordDao;

    @Transactional
    @Override
    public void onMessage(SubmitOrderSuccessEvent submitOrderSuccessEvent) {
        log.info("【下单成功消息】开始消费：{}", JSON.toJSONString(submitOrderSuccessEvent));

        // 1、【幂等性】插入本地消息记录表
        MQRecordEntity mqRecordEntity = new MQRecordEntity();
        mqRecordEntity.setTopic(Constants.Topic.DELAY_CLOSE_ORDER_TOPIC);
        mqRecordEntity.setMessageId(String.valueOf(submitOrderSuccessEvent.getOrderId()));
        mqRecordEntity.setMessage(submitOrderSuccessEvent);
        mqRecordDao.insert(mqRecordEntity);

        // 2、创建预支付
        // 3、写入支付服务订单流水
        // 4、订单服务订单状态修改
        // 5、【定时任务】支付服务、订单服务订单状态对账
    }

}
