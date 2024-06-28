package com.cyk.mall.stock.config;

import com.cyk.mall.common.enums.Exchanges;
import com.cyk.mall.common.enums.Queues;
import com.cyk.mall.common.enums.RoutingKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;

@Configuration
@Slf4j
public class RabbitMQConfig {

    @Bean("jsonMessageConverter")
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         @Qualifier("jsonMessageConverter") MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        // 设置confirm回调，只要消息抵达Broker就ack=true
        // correlationData：当前消息的唯一关联数据(这个是消息的唯一id)
        // ack：消息是否成功收到
        // cause：失败的原因
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("confirm...correlationData:[{}], ack:[{}], cause:[{}]", correlationData, ack, cause);
            if (!ack) {
                //todo 入库，消息补偿
            }
        });

        // 只要消息没有投递给指定的队列，就触发这个失败回调
        // message：投递失败的消息详细信息
        // replyCode：回复的状态码
        // replyText：回复的文本内容
        // exchange：当时这个消息发给哪个交换机
        // routingKey：当时这个消息用哪个路由键
        rabbitTemplate.setReturnsCallback(returnedMessage -> log.info("Fail Message to queue... message:[{}], replyCode:[{}], replyText:[{}], exchange:[{}], routingKey:[{}]",
                returnedMessage.getMessage(), returnedMessage.getReplyCode(), returnedMessage.getReplyText(),
                returnedMessage.getExchange(), returnedMessage.getRoutingKey()));
        return rabbitTemplate;
    }

    @Bean("stockEventExchange")
    public Exchange stockEventExchange() {
        return new TopicExchange(Exchanges.STOCK_EVENT_EXCHANGE.getName(), true, false);
    }

    /**
     * 延迟队列
     */
    @Bean("stockDelayQueue")
    public Queue stockDelayQueue() {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", Exchanges.STOCK_EVENT_EXCHANGE.getName());
        arguments.put("x-dead-letter-routing-key", RoutingKey.STOCK_RELEASE_STOCK.getName());
        // 消息过期时间 2分钟
        arguments.put("x-message-ttl", 120000);

        return new Queue(Queues.STOCK_DELAY_QUEUE.getName(), true, false, false, arguments);
    }

    @Bean
    public Binding stockLockBinding(@Qualifier("stockEventExchange") Exchange stockEventExchange,
                                    @Qualifier("stockDelayQueue") Queue stockDelayQueue) {
        return BindingBuilder.bind(stockDelayQueue)
                .to(stockEventExchange)
                .with(RoutingKey.STOCK_LOCK_STOCK.getName())
                .noargs();
    }

    /**
     * 普通队列
     */
    @Bean("stockReleaseQueue")
    public Queue stockReleaseQueue() {
        return new Queue(Queues.STOCK_RELEASE_QUEUE.getName(), true, false, false);
    }

    @Bean
    public Binding stockReleaseBinding(@Qualifier("stockEventExchange") Exchange stockEventExchange,
                                       @Qualifier("stockReleaseQueue") Queue stockReleaseQueue) {
        return BindingBuilder.bind(stockReleaseQueue)
                .to(stockEventExchange)
                .with("stock.release.#")
                .noargs();
    }
}
