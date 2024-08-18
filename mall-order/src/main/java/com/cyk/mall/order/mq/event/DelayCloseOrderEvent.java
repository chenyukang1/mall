package com.cyk.mall.order.mq.event;

import com.cyk.mall.common.mq.model.MessageWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The class DelayCloseOrderEvent.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DelayCloseOrderEvent extends MessageWrapper {

    private static final long serialVersionUID = -8682702235897426287L;

    /**
     * 业务仿重ID - 确保幂等
     */
    private String outBusinessNo;

    /**
     * 订单流水号
     */
    private String orderSn;
}
