package com.cyk.mall.pay.domain.po;

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
@TableName("mq_record")
public class MQRecordEntity {

    /**
     * 自增ID
     */
    @TableId
    private Integer id;

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
}
