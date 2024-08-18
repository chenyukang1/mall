package com.cyk.mall.common.constant;

import lombok.Getter;

/**
 * The class Constants.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
public class Constants {

    public static class Topic {
        public static final String DELAY_CLOSE_ORDER_TOPIC = "mall_topic_delay_close_order";
        public static final String UNLOCK_STOCK_TOPIC = "mall_topic_unlock_stock";
    }

    public static class ConsumerGroup {
        public static final String DELAY_CLOSE_ORDER_CONSUMER_GROUP = "mall_consumer_group_delay_close_order";
    }

    public static class MQTaskStatus {
        public static final String CREATE = "create";
        public static final String COMPLETED = "completed";
        public static final String FAIL= "fail";
    }

    @Getter
    public enum OrderStatus {
        /**
         * 创建
         */
        CREATE(0),
        /**
         * 已完成
         */
        COMPLETED(1),
        /**
         * 已取消
         */
        CANCELED(2);

        private final Integer status;

        OrderStatus(Integer status) {
            this.status = status;
        }
    }

    public static class MQProducerGroup {
        public static final String PRODUCER_TX_GROUP = "mall-tx-producer-group";
    }
}
