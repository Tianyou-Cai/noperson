-- 修复payment_log和payment_order表的字符集问题
-- 将字符集从armscii8修改为utf8mb4，支持中文字符

-- 修改payment_log表的字符集
ALTER TABLE `payment_log` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 修改payment_order表的字符集
ALTER TABLE `payment_order` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 如果上面的命令因为外键约束失败，可以使用以下逐字段修改的方式：

-- payment_log表字段修改
ALTER TABLE `payment_log` MODIFY `order_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
ALTER TABLE `payment_log` MODIFY `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
ALTER TABLE `payment_log` MODIFY `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
ALTER TABLE `payment_log` MODIFY `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
ALTER TABLE `payment_log` MODIFY `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL;
ALTER TABLE `payment_log` MODIFY `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL;

-- payment_order表字段修改
ALTER TABLE `payment_order` MODIFY `order_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
ALTER TABLE `payment_order` MODIFY `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
ALTER TABLE `payment_order` MODIFY `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
ALTER TABLE `payment_order` MODIFY `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
ALTER TABLE `payment_order` MODIFY `out_trade_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL;
ALTER TABLE `payment_order` MODIFY `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL;
