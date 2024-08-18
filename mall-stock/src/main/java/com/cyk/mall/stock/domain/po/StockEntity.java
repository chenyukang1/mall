package com.cyk.mall.stock.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * 
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 16:29:27
 */
@Data
@TableName("tab_stock")
public class StockEntity implements Serializable {

	private static final long serialVersionUID = -6660452917304832759L;

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
	 * 商品库存
	 */
	private Integer stockCount;

	/**
	 * 剩余库存
	 */
	private Integer stockCountSurplus;
}
