package com.cyk.mall.common.domain.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderTo {

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
