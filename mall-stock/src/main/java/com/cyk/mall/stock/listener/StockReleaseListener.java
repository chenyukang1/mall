package com.cyk.mall.stock.listener;

import com.cyk.mall.common.to.StockTo;
import com.cyk.mall.stock.service.StockService;
import com.cyk.mall.stock.domain.to.StockLockTo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@RabbitListener(queues = "stock.release.queue")
@Slf4j
public class StockReleaseListener {

    @Autowired
    private StockService stockService;

    @RabbitHandler
    public void tryStockRelease(StockLockTo stockLockTo, Message message, Channel channel) throws IOException, InterruptedException {
        log.info("******锁库存后，延迟收到尝试回滚库存的消息******");
        try {

            // 尝试回滚库存
            stockService.unlockStock(stockLockTo);
            // 手动 ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("尝试回滚库存消费失败", e);
            TimeUnit.SECONDS.sleep(5);
            // 解锁失败 将消息重新放回队列，让别人消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(StockTo stockTo, Message message, Channel channel) throws IOException, InterruptedException {
        log.info("******收到订单关闭，准备解锁库存的消息******");
        try {
            stockService.unlockStock(stockTo);
            // 手动 ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("订单关闭，准备解锁库存消费失败", e);
            TimeUnit.SECONDS.sleep(5);
            // 解锁失败 将消息重新放回队列，让别人消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
