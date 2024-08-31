package com.cyk.mall.common.mq.handler;

/**
 * The interface ITransactionMsgHandler.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/19
 */
@FunctionalInterface
public interface ITransactionMsgHandler {

    boolean handle(String transactionId);
}
