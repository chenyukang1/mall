package com.cyk.mall.order.domain.req;

import lombok.Data;
import lombok.ToString;

/**
 * The class SubmitOrderReq.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
@Data
@ToString
public class SubmitOrderReq {

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
}
