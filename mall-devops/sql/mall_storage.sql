CREATE DATABASE /*!32312 IF NOT EXISTS*/`mall_storage` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `mall_storage`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 库存服务DB执行
CREATE TABLE `tab_storage`
(
    `id`         bigint(11) NOT NULL AUTO_INCREMENT,
    `product_id` bigint(11) DEFAULT NULL COMMENT '产品id',
    `total`      int(11) DEFAULT NULL COMMENT '总库存',
    `used`       int(11) DEFAULT NULL COMMENT '已用库存',
    `version`    int(11) NOT NULL DEFAULT 0 COMMENT '乐观锁控制字段',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
INSERT INTO `tab_storage` (`product_id`, `total`, `used`)
VALUES ('1', '96', '4');
INSERT INTO `tab_storage` (`product_id`, `total`, `used`)
VALUES ('2', '100', '0');

CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
