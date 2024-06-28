package com.cyk.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyk.mall.common.enums.Exchanges;
import com.cyk.mall.common.enums.RoutingKey;
import com.cyk.mall.common.to.StockTo;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.Query;
import com.cyk.mall.common.utils.R;
import com.cyk.mall.order.dao.OrderDao;
import com.cyk.mall.order.entity.OrderEntity;
import com.cyk.mall.order.feign.StorageFeignService;
import com.cyk.mall.order.service.OrderService;
import com.cyk.mall.order.to.OrderTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;


@Service("orderService")
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private StorageFeignService storageFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean save(long userId, long productId) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);
        orderEntity.setProductId(productId);
        orderEntity.setCount(1);
        orderEntity.setMoney(BigDecimal.valueOf(80));
        orderEntity.setStatus(0);

        return save(orderEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submitOrder(long userId, long productId, long used) {
        // 1、创建订单，写db
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);
        orderEntity.setProductId(productId);
        orderEntity.setCount(1);
        orderEntity.setMoney(BigDecimal.valueOf(80));
        orderEntity.setStatus(0);
        save(orderEntity);

        // 2、调用远程锁库存，失败直接回滚
        // 如果这步因为网络原因异常，订单回滚了，但库存锁成功了怎么办？
        // - 库存回滚逻辑保证最终一致性：锁库存后，发送消息到mq延迟队列，判断是否需要回滚库存
        R res = storageFeignService.lockStock(productId, used, orderEntity.getId());
        if (res.getCode() != 0) {
            log.info("锁库存失败");
            throw new RuntimeException("锁库存失败");
        }
        // 3、发送到订单延迟队列，超时关单
        OrderTo orderTo = new OrderTo();
        BeanUtils.copyProperties(orderEntity, orderTo);
        orderTo.setUsed(used);
        rabbitTemplate.convertAndSend(Exchanges.ORDER_EVENT_EXCHANGE.getName(),
                RoutingKey.ORDER_CREATE_ORDER.getName(), orderTo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void closeOrder(OrderTo orderTo) {
        OrderEntity exist = getById(orderTo.getId());
        // 订单未完成，则取消
        if (exist.getStatus() == 0) {
            OrderEntity update = new OrderEntity();
            update.setId(orderTo.getId());
            update.setStatus(3);
            updateById(update);

            R r = storageFeignService.productInfo(orderTo.getProductId());
            if (r.getCode() != 0) {
                // 远程调用失败就回滚db，消息重新入队
                throw new RuntimeException("调用库存服务失败！");
            }
            StockTo stockTo = r.getData("storage", new TypeReference<StockTo>() {});
            stockTo.setRollback(orderTo.getUsed());
            // 发消息给回滚库存队列，回滚该订单库存
            rabbitTemplate.convertAndSend(Exchanges.STOCK_EVENT_EXCHANGE.getName(),
                    RoutingKey.STOCK_RELEASE_ORDER.getName(), stockTo);
        }
    }

}