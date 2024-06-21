package com.cyk.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 
 *
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 12:28:27
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean save(long userId, long productId);
}

