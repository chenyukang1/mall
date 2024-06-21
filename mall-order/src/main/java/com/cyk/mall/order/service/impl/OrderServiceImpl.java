package com.cyk.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.Query;
import com.cyk.mall.order.dao.OrderDao;
import com.cyk.mall.order.entity.OrderEntity;
import com.cyk.mall.order.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

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

}