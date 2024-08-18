package com.cyk.mall.order.mq.producer;

import com.cyk.mall.common.constant.Constants;
import com.cyk.mall.common.mq.model.BaseSendExtendDTO;
import com.cyk.mall.common.mq.producer.AbstractCommonSendProducer;
import com.cyk.mall.order.mq.event.UnLockStockEvent;
import org.springframework.stereotype.Service;

/**
 * The class DelayCloseOrderPublisher.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
@Service
public class UnlockStockProducer extends AbstractCommonSendProducer<UnLockStockEvent> {

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(UnLockStockEvent unLockStockEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("回滚库存消息")
                .topic(Constants.Topic.UNLOCK_STOCK_TOPIC)
                .keys(String.valueOf(unLockStockEvent.getSku()))
                .delayLevel(0)
                .sendTimeout(1000L)
                .build();
    }
}
