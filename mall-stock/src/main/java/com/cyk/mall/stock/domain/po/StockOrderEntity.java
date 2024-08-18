package com.cyk.mall.stock.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * The class StockOrderEntity.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/10
 */
@Data
@TableName("tab_stock_order")
public class StockOrderEntity implements Serializable {

    private static final long serialVersionUID = 7846862557483975196L;

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
     * 订单流水号
     */
    private String orderSn;

    /**
     * 库存扣减数量
     */
    private Integer stockSubtractionCount;
}
