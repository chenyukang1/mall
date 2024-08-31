package com.cyk.mall.order.mq.transaction.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyk.mall.common.mq.handler.ITransactionMsgHandler;
import com.cyk.mall.order.dao.MQTransactionDao;
import com.cyk.mall.order.domain.po.MQTransactionEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * The class TransactionMsgListener.
 *
 * @author chenyukang
 * @email chen.yukang @qq.com
 * @date 2024 /8/18
 */
@Slf4j
@RocketMQTransactionListener
public class TransactionMsgListener implements RocketMQLocalTransactionListener {

    @Resource
    private MQTransactionDao mqTransactionDao;

    /**
     * 执行本地事务（在发送消息成功时执行）
     *
     * @param message message
     * @param arg arg
     * @return commit or rollback or unknown
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        // 1、获取事务ID
        String transactionId = null;
        try {
            transactionId = Optional.ofNullable(message.getHeaders().get("rocketmq_TRANSACTION_ID"))
                    .map(Object::toString)
                    .orElseThrow(() -> new RuntimeException("事务消息回滚，transactionId 为空"));
            // 2、判断传入函数对象是否为空，如果为空代表没有要执行的业务直接抛弃消息
            if (arg == null) {
                //返回ROLLBACK状态的消息会被丢弃
                log.info("事务消息回滚，没有需要处理的业务 transactionId={}", transactionId);
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            // 将Object arg转换成TransactionMsgHandler对象
            ITransactionMsgHandler ITransactionMsgHandler = (ITransactionMsgHandler) arg;
            // 执行业务 事务也会在function.apply中执行
            boolean res = ITransactionMsgHandler.handle(transactionId);
            if (res) {
                log.info("事务提交，消息正常处理 transactionId={}", transactionId);
                //返回COMMIT状态的消息会立即被消费者消费到
                return RocketMQLocalTransactionState.COMMIT;
            }
        } catch (Exception e) {
            log.info("出现异常 返回ROLLBACK transactionId={}", transactionId);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    /**
     * 事务回查机制，检查本地事务的状态
     *
     * @param message message
     * @return RocketMQLocalTransactionState
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        String transactionId = Optional.ofNullable(message.getHeaders().get("rocketmq_TRANSACTION_ID"))
                .map(Object::toString)
                .orElseThrow(() -> new RuntimeException("事务消息回滚，transactionId 为空"));

        MQTransactionEntity mqTransactionEntity = mqTransactionDao.selectOne(
                new QueryWrapper<MQTransactionEntity>().eq("transaction_id", transactionId));
        if (mqTransactionEntity == null) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        return RocketMQLocalTransactionState.COMMIT;
    }
}

