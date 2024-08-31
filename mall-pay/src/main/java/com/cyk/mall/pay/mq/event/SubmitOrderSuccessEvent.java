package com.cyk.mall.pay.mq.event;

import com.cyk.mall.common.mq.model.MessageWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * The class SubmitOrderSuccessEvent.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SubmitOrderSuccessEvent extends MessageWrapper {

    private static final long serialVersionUID = -707237574962540047L;

    /**
     * 业务仿重ID - 确保幂等
     */
    private String uuid;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 下单总金额
     */
    private BigDecimal amount;
}
