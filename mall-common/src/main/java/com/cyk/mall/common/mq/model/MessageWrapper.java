package com.cyk.mall.common.mq.model;

import lombok.Data;

import java.io.Serializable;

/**
 * The class MessageWrapper.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/7/10
 **/
@Data
public class MessageWrapper implements Serializable {

    private static final long serialVersionUID = 2996975205530895522L;
    /**
     * 消息发送时间
     */
    private final Long timestamp = System.currentTimeMillis();
}
