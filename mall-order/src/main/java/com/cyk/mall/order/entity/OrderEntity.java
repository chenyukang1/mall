package com.cyk.mall.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * 
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 12:28:27
 */
@Data
@TableName("tab_order")
public class OrderEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 产品id
	 */
	private Long productId;
	/**
	 * 数量
	 */
	private Integer count;
	/**
	 * 金额
	 */
	private BigDecimal money;
	/**
	 * 订单状态：0：创建中；1：已完成
	 */
	private Integer status;

}
