package com.cyk.mall.common.mq.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class BaseSendExtendDTO.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/7/10
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class BaseSendExtendDTO {

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 主题
     */
    private String topic;

    /**
     * 标签
     */
    private String tag;

    /**
     * 业务标识
     */
    private String keys;

    /**
     * 发送消息超时时间
     */
    private Long sendTimeout;

    /**
     * 延迟消息
     */
    private Integer delayLevel;
}
