package com.cyk.mall.order.mq.producer;

import com.cyk.mall.common.constant.Constants;
import com.cyk.mall.common.mq.model.BaseSendExtendDTO;
import com.cyk.mall.common.mq.producer.AbstractCommonSendProducer;
import com.cyk.mall.order.mq.event.DelayCloseOrderEvent;
import org.springframework.stereotype.Service;

/**
 * The class DelayCloseOrderPublisher.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
@Service
public class DelayCloseOrderProducer extends AbstractCommonSendProducer<DelayCloseOrderEvent> {

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(DelayCloseOrderEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("延迟关单消息")
                .topic(Constants.Topic.DELAY_CLOSE_ORDER_TOPIC)
                .keys(messageSendEvent.getOrderSn())
                .delayLevel(16)
                .sendTimeout(1000L)
                .build();
    }
}
