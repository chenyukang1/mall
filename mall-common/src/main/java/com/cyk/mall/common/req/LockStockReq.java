package com.cyk.mall.common.req;

import lombok.Data;

/**
 * The class LockStockReq.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/10
 */
@Data
public class LockStockReq {

    private String orderSn;

    private Long sku;

    private Integer lockCount;
}
