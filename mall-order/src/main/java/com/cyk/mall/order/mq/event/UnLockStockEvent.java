package com.cyk.mall.order.mq.event;

import com.cyk.mall.common.mq.model.MessageWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The class UnLockStockEvent.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UnLockStockEvent extends MessageWrapper {

    private static final long serialVersionUID = -6234149779546940825L;

    /**
     * 业务仿重ID - 确保幂等
     */
    private String uuid;

    /**
     * 商品sku
     */
    private Long sku;

    /**
     * 商品数量
     */
    private Integer count;
}
