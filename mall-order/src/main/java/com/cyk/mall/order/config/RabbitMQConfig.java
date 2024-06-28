package com.cyk.mall.order.config;

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

import static com.cyk.mall.common.enums.Queues.ORDER_DELAY_QUEUE;

@Configuration
@Slf4j
public class RabbitMQConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        // 设置confirm回调，只要消息抵达Broker就ack=true
        // correlationData：当前消息的唯一关联数据(这个是消息的唯一id)
        // ack：消息是否成功收到
        // cause：失败的原因
        rabbitTemplate.setConfirmCallback((correlationData,ack,cause) -> {
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

    @Bean("orderEventExchange")
    public Exchange orderEventExchange() {
        return new TopicExchange(Exchanges.ORDER_EVENT_EXCHANGE.getName(), true, false);
    }

    @Bean("orderDelayQueue")
    public Queue orderDelayQueue() {
        /*
            Queue(String name,  队列名字
            boolean durable,  是否持久化
            boolean exclusive,  是否排他
            boolean autoDelete, 是否自动删除
            Map<String, Object> arguments) 属性
         */
        HashMap<String, Object> arguments = new HashMap<>();
        // 绑定死信消息路由
        arguments.put("x-dead-letter-exchange", Exchanges.ORDER_EVENT_EXCHANGE.getName());
        arguments.put("x-dead-letter-routing-key", RoutingKey.ORDER_RELEASE_ORDER.getName());
        arguments.put("x-message-ttl", 60000); // 消息过期时间 1分钟

        return new Queue(ORDER_DELAY_QUEUE.getName(), true, false, false, arguments);
    }

    @Bean
    public Binding orderCreateBinding(@Qualifier("orderEventExchange") Exchange orderEventExchange,
                                      @Qualifier("orderDelayQueue") Queue orderDelayQueue) {
        return BindingBuilder.bind(orderDelayQueue)
                .to(orderEventExchange)
                .with(RoutingKey.ORDER_CREATE_ORDER.getName()).noargs();
    }

    @Bean("orderReleaseQueue")
    public Queue orderReleaseQueue() {
        return new Queue(Queues.ORDER_RELEASE_QUEUE.getName(), true, false, false);
    }

    @Bean
    public Binding orderReleaseBinding(@Qualifier("orderEventExchange") Exchange orderEventExchange,
                                       @Qualifier("orderReleaseQueue") Queue orderReleaseQueue) {
        return BindingBuilder.bind(orderReleaseQueue)
                .to(orderEventExchange)
                .with(RoutingKey.ORDER_RELEASE_ORDER.getName()).noargs();
    }
}
