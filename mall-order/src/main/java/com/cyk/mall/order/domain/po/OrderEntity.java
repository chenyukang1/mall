package com.cyk.mall.order.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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

	private static final long serialVersionUID = 3463717735630595350L;

	/**
	 * 自增ID
	 */
	@TableId
	private Long id;

	/**
	 * 商品sku
	 */
	private Long sku;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 订单流水号
	 */
	private String orderSn;

	/**
	 * 业务仿重ID
	 */
	private String outBusinessNo;

	/**
	 * 商品数量
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
