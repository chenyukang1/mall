package com.cyk.mall.order.mq.producer;

import com.cyk.mall.common.domain.constant.Constants;
import com.cyk.mall.common.mq.model.BaseSendExtendDTO;
import com.cyk.mall.common.mq.producer.AbstractCommonSendProducer;
import com.cyk.mall.order.mq.event.RollbackStockEvent;
import org.springframework.stereotype.Service;

/**
 * The class DelayCloseOrderPublisher.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
@Service
public class UnlockStockProducer extends AbstractCommonSendProducer<RollbackStockEvent> {

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(RollbackStockEvent rollbackStockEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("回滚库存消息")
                .topic(Constants.Topic.UNLOCK_STOCK_TOPIC)
                .keys(String.valueOf(rollbackStockEvent.getSku()))
                .delayLevel(0)
                .sendTimeout(1000L)
                .build();
    }
}
