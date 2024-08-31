package com.cyk.mall.common.domain.constant;

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
        public static final String SUBMIT_ORDER_SUCCESS_TOPIC = "mall_topic_submit_order_success";
    }

    public static class ConsumerGroup {
        public static final String DELAY_CLOSE_ORDER_CONSUMER_GROUP = "mall_consumer_group_delay_close_order";
        /**
         * 下单成功支付cg
         */
        public static final String SUBMIT_ORDER_SUCCESS_PAY_CONSUMER_GROUP = "mall_consumer_group_submit_order_success_pay";
        /**
         * 下单成功积分cg
         */
        public static final String SUBMIT_ORDER_SUCCESS_CREDIT_CONSUMER_GROUP = "mall_consumer_group_submit_order_success_credit";
        /**
         * 下单成功购物车cg
         */
        public static final String SUBMIT_ORDER_SUCCESS_CART_CONSUMER_GROUP = "mall_consumer_group_submit_order_success_cart";
        /**
         * 下单成功物流cg
         */
        public static final String SUBMIT_ORDER_SUCCESS_LOGISTICS_CONSUMER_GROUP = "mall_consumer_group_submit_order_success_logistics";
    }

    public static class MQTaskStatus {
        public static final String CREATE = "create";
        public static final String COMPLETED = "completed";
        public static final String FAIL= "fail";
    }

    public static class IdGeneratorConstants {
        public static final String UUID_GENERATOR = "UUIDGenerator";
    }

    @Getter
    public enum IdGeneratorType {
        UUID_GENERATOR(IdGeneratorConstants.UUID_GENERATOR);

        private final String type;

        IdGeneratorType(String type) {
            this.type = type;
        }
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
