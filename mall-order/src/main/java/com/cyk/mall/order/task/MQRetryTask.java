package com.cyk.mall.order.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyk.mall.common.domain.constant.Constants;
import com.cyk.mall.common.mq.model.MessageWrapper;
import com.cyk.mall.common.mq.producer.AbstractCommonSendProducer;
import com.cyk.mall.order.dao.MQTaskDao;
import com.cyk.mall.order.domain.po.MQTaskEntity;
import com.cyk.mall.common.support.holder.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * The class MQRetryTask.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/10
 */
@Component
@Slf4j
public class MQRetryTask {

    @Resource
    private MQTaskDao MQTaskDao;

    @Resource
    private ApplicationContextHolder applicationContextHolder;

    /**
     * 重发MQ任务
     */
    @Scheduled(cron = "0/5 * *  * * ?")
    public void retryTask() {
        QueryWrapper<MQTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("state", Constants.MQTaskStatus.COMPLETED);
        List<MQTaskEntity> taskEntities = MQTaskDao.selectList(queryWrapper);
        for (MQTaskEntity MQTaskEntity : taskEntities) {
            MessageWrapper messageWrapper = MQTaskEntity.getMessage();
            AbstractCommonSendProducer commonSendProducer = applicationContextHolder.getApplicationContext()
                    .getBean(AbstractCommonSendProducer.class, MQTaskEntity.getProducer());
            SendResult sendResult = commonSendProducer.syncSend(messageWrapper);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                log.info("消息 {} 发送成功", messageWrapper);
                MQTaskEntity updateEntity = new MQTaskEntity();
                updateEntity.setState(Constants.MQTaskStatus.COMPLETED);
                QueryWrapper<MQTaskEntity> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("message_id", MQTaskEntity.getMessageId());
                MQTaskDao.update(updateEntity, queryWrapper1);
            }
        }
    }
}
