package com.cyk.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.order.domain.po.OrderEntity;
import com.cyk.mall.order.domain.req.SubmitOrderReq;

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

    void submitOrder(SubmitOrderReq submitOrderReq);

    void submitOrderV2(SubmitOrderReq submitOrderReq);
}

