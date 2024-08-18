CREATE DATABASE /*!32312 IF NOT EXISTS*/`mall_order` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `mall_order`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 订单流水表
CREATE TABLE `tab_order`
(
    `id`              bigint(11) NOT NULL AUTO_INCREMENT,
    `sku`             bigint(11) NOT NULL COMMENT '商品sku',
    `user_id`         varchar(32)    NOT NULL COMMENT '用户ID',
    `order_sn`        varchar(12)    NOT NULL COMMENT '订单流水号',
    `out_business_no` varchar(64)    NOT NULL COMMENT '业务仿重ID - 确保幂等',
    `count`           int(11) NOT NULL COMMENT '商品数量',
    `money`           decimal(11, 0) NOT NULL COMMENT '交易金额',
    `status`          int(1) NOT NULL DEFAULT 0 COMMENT '订单状态：0：创建中；1：已完成',
    `create_time`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_order_sn` (`order_sn`),
    UNIQUE KEY `uq_out_business_no` (`out_business_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单流水表';

-- 消息任务表
CREATE TABLE `mq_task`
(
    `id`          int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `user_id`     varchar(32)  NOT NULL COMMENT '用户ID',
    `topic`       varchar(32)  NOT NULL COMMENT '消息主题',
    `message_id`  varchar(11)  DEFAULT NULL COMMENT '消息编号',
    `message`     varchar(512) NOT NULL COMMENT '消息主体',
    `producer`    varchar(16)  NOT NULL COMMENT '消息生产者',
    `state`       varchar(16)  NOT NULL DEFAULT 'create' COMMENT '任务状态；create-创建、completed-完成、fail-失败',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_message_id` (`message_id`),
    KEY           `idx_state` (`state`),
    KEY           `idx_create_time` (`update_time`)
) ENGINE=InnoDB AUTO_INCREMENT=234 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息任务表';

-- 消息记录表
CREATE TABLE `mq_record`
(
    `id`          int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `topic`       varchar(32)  NOT NULL COMMENT '消息主题',
    `message_id`  varchar(11)  DEFAULT NULL COMMENT '消息编号',
    `message`     varchar(512) NOT NULL COMMENT '消息主体',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_message_id` (`message_id`),
    KEY           `idx_create_time` (`update_time`)
) ENGINE=InnoDB AUTO_INCREMENT=234 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息记录表';