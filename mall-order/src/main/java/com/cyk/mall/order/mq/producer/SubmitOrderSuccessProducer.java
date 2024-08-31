package com.cyk.mall.order.mq.producer;

import com.cyk.mall.common.domain.constant.Constants;
import com.cyk.mall.common.mq.model.BaseSendExtendDTO;
import com.cyk.mall.common.mq.producer.AbstractCommonSendProducer;
import com.cyk.mall.order.mq.event.SubmitOrderSuccessEvent;
import org.springframework.stereotype.Service;

/**
 * The class DelayCloseOrderPublisher.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
@Service
public class SubmitOrderSuccessProducer extends AbstractCommonSendProducer<SubmitOrderSuccessEvent> {

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(SubmitOrderSuccessEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("下单成功消息")
                .topic(Constants.Topic.SUBMIT_ORDER_SUCCESS_TOPIC)
                .keys(String.valueOf(messageSendEvent.getOrderId()))
                .delayLevel(1)
                .sendTimeout(1000L)
                .build();
    }
}
