package com.cyk.mall.order.domain.aggregate;

import lombok.Data;
import lombok.ToString;

/**
 * 下单聚合对象
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/19
 */
@Data
@ToString
public class SubmitOrderAggregate {

    /**
     * 商品sku
     */
    private Long sku;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 商品数量
     */
    private Integer count;

    /**
     * 金额
     */
    private String money;

    /**
     * 订单流水号
     */
    private String orderSn;

    /**
     * 业务仿重ID
     */
    private String outBusinessNo;
}
