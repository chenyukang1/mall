package com.cyk.mall.order.listener;

import com.cyk.mall.order.entity.OrderEntity;
import com.cyk.mall.order.service.OrderService;
import com.cyk.mall.order.to.OrderTo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RabbitListener(queues = "order.release.queue")
@Service
@Slf4j
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(OrderTo orderTo, Channel channel, Message message) throws IOException, InterruptedException {
        log.info("收到过期的订单信息，准备关闭订单 {}", orderTo.getId());
        try {
            orderService.closeOrder(orderTo);
            // 手动 ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("关闭订单消费失败", e);
            TimeUnit.SECONDS.sleep(5);
            // 关单失败 将消息重新放回队列，让别人消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
