package com.cyk.mall.common.mq.producer;

import com.cyk.mall.common.mq.model.BaseSendExtendDTO;
import com.cyk.mall.common.mq.model.MessageWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * The class AbstractCommonSendProducer.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/7/10
 **/
public abstract class AbstractCommonSendProducer<T extends MessageWrapper> {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public final SendResult syncSend(T messageSendEvent) {
        BaseSendExtendDTO baseSendExtendDTO = buildBaseSendExtendParam(messageSendEvent);
        StringBuilder destination = new StringBuilder(baseSendExtendDTO.getTopic());
        String tag = baseSendExtendDTO.getTag();
        if (StringUtils.isNotEmpty(tag)) {
            destination.append(":").append(tag);
        }
        return rocketMQTemplate.syncSend(destination.toString(),
                buildMessage(messageSendEvent, baseSendExtendDTO),
                baseSendExtendDTO.getSendTimeout(),
                baseSendExtendDTO.getDelayLevel()
        );
    }

    public final SendResult sendMessageInTransaction(T messageSendEvent, Object arg) {
        BaseSendExtendDTO baseSendExtendDTO = buildBaseSendExtendParam(messageSendEvent);
        StringBuilder destination = new StringBuilder(baseSendExtendDTO.getTopic());
        String tag = baseSendExtendDTO.getTag();
        if (StringUtils.isNotEmpty(tag)) {
            destination.append(":").append(tag);
        }
        return rocketMQTemplate.sendMessageInTransaction(destination.toString(),
                buildMessage(messageSendEvent, baseSendExtendDTO),
                arg
        );
    }

    /**
     * 构建消息发送事件基础扩充属性实体
     *
     * @param messageSendEvent 消息发送事件
     * @return 扩充属性实体
     */
    protected abstract BaseSendExtendDTO buildBaseSendExtendParam(T messageSendEvent);

    /**
     * 构建消息基本参数，请求头、Keys...
     *
     * @param messageSendEvent 消息发送事件
     * @param requestParam     扩充属性实体
     * @return 消息基本参数
     */
    protected Message<?> buildMessage(T messageSendEvent, BaseSendExtendDTO requestParam) {
        String keys = StringUtils.isEmpty(requestParam.getKeys()) ?
                UUID.randomUUID().toString() : requestParam.getKeys();
        return MessageBuilder
                .withPayload(messageSendEvent)
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();
    }

}
