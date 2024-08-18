package com.cyk.mall.order.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cyk.mall.common.mq.model.MessageWrapper;
import lombok.Data;

/**
 * The class MQTaskEntity.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
@Data
@TableName("mq_task")
public class MQTaskEntity {

    /**
     * 自增ID
     */
    @TableId
    private Integer id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 消息发布者
     */
    private String producer;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 消息编号
     */
    private String messageId;

    /**
     * 消息主体
     */
    private MessageWrapper message;

    /**
     * 任务状态；create-创建、completed-完成、fail-失败
     */
    private String state;
}
