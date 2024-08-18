package com.cyk.mall.order.mq.transaction.listener;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * The class DelayCloseOrderListener.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/11
 */
@Component
@RocketMQTransactionListener
public class DelayCloseOrderListener implements RocketMQLocalTransactionListener {

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        return null;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        return null;
    }
}
