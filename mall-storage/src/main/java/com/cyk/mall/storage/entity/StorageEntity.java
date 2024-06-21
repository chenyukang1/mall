package com.cyk.mall.storage.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 16:29:27
 */
@Data
@TableName("tab_storage")
public class StorageEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 产品id
	 */
	private Long productId;
	/**
	 * 总库存
	 */
	private Integer total;
	/**
	 * 已用库存
	 */
	private Integer used;

}
