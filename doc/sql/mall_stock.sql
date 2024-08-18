CREATE DATABASE /*!32312 IF NOT EXISTS*/`mall_stock` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `mall_stock`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 库存表
CREATE TABLE `tab_stock`
(
    `id`                  bigint(11) NOT NULL AUTO_INCREMENT,
    `sku`                 bigint(11) NOT NULL COMMENT '商品sku',
    `stock_count`         int(11) NOT NULL COMMENT '商品库存',
    `stock_count_surplus` int(11) NOT NULL COMMENT '剩余库存',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO `tab_stock` (`sku`, `stock_count`, `stock_count_surplus`) VALUES ('1', '100', '96');
INSERT INTO `tab_stock` (`sku`, `stock_count`, `stock_count_surplus`) VALUES ('2', '100', '100');

-- 库存订单流水表，用于对账
CREATE TABLE `tab_stock_order`
(
    `id`                      bigint(11) NOT NULL AUTO_INCREMENT,
    `sku`                     bigint(11) NOT NULL COMMENT '商品sku',
    `order_sn`                varchar(12) NOT NULL COMMENT '订单流水号',
    `stock_subtraction_count` int(11) NOT NULL COMMENT '库存扣减数量',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_sku_order_sn` (`sku`, `order_sn`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
