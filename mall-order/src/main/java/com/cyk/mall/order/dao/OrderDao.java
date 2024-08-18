package com.cyk.mall.order.dao;

import com.cyk.mall.order.domain.po.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 12:28:27
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
