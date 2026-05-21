USE noperson;

-- 创建银行卡表
CREATE TABLE IF NOT EXISTS `bank_card` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `bank_name` VARCHAR(100) NOT NULL COMMENT '银行名称',
    `card_number` VARCHAR(50) NOT NULL COMMENT '银行卡号',
    `card_holder` VARCHAR(50) NOT NULL COMMENT '持卡人姓名',
    `branch` VARCHAR(200) COMMENT '开户支行',
    `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否为默认银行卡',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_bank_card_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='银行卡表';

-- 更新transaction_record表，添加更多交易类型支持
ALTER TABLE `transaction_record` MODIFY COLUMN `transaction_type` VARCHAR(20) NOT NULL COMMENT '交易类型：INCOME（收入）、EXPENSE（支出）、RECHARGE（充值）、WITHDRAW（提现）';
