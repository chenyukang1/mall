package com.cyk.mall.order.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * The class MQTransactionEntity.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/19
 */
@Data
@TableName("mq_transaction")
public class MQTransactionEntity {

    /**
     * 自增ID
     */
    @TableId
    private Integer id;

    /**
     * 消息事务ID
     */
    private String transactionId;

}
