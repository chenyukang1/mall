package com.cyk.mall.common.to;

import lombok.Data;

@Data
public class StockTo {
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

    private Long rollback;

    /**
     * 乐观锁控制字段
     */
    private Integer version;
}
