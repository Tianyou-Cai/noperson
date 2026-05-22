-- B2C 商城模式数据库设计
-- 更新时间：2026-05-22

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `user_type` varchar(20) NOT NULL,
  `status` int NOT NULL DEFAULT 1,
  `balance` decimal(10,2) DEFAULT 0.00,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_login_time` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 服务分类表
DROP TABLE IF EXISTS `service_category`;
CREATE TABLE `service_category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) NOT NULL,
  `parent_id` int DEFAULT 0,
  `icon` varchar(50) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `sort` int NOT NULL DEFAULT 0,
  `is_hot` int NOT NULL DEFAULT 0,
  `status` int NOT NULL DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务分类表';

INSERT INTO `service_category` VALUES 
(1, '农药喷洒', 0, '🌾', '高效均匀的农药喷洒服务', 1, 1, 1, NOW(), NOW()),
(2, '播种服务', 0, '🌱', '精准量化播种', 2, 0, 1, NOW(), NOW()),
(3, '施肥作业', 0, '💊', '科学配方施肥', 3, 0, 1, NOW(), NOW()),
(4, '农情监测', 0, '📊', '遥感数据分析', 4, 0, 1, NOW(), NOW()),
(5, '除草服务', 0, '🌿', '靶向精准除草', 5, 0, 1, NOW(), NOW()),
(6, '植保托管', 0, '🤝', '全程托管服务', 6, 1, 1, NOW(), NOW());

-- 服务商品表
DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
  `service_id` bigint NOT NULL AUTO_INCREMENT,
  `provider_id` bigint NOT NULL COMMENT '服务提供方 ID',
  `provider_type` varchar(20) NOT NULL COMMENT 'flyer-飞手 owner-机主',
  `category_id` int DEFAULT NULL,
  `service_name` varchar(100) NOT NULL,
  `service_description` varchar(500) DEFAULT NULL,
  `service_details` text,
  `service_type` varchar(50) DEFAULT NULL,
  `crop_types` varchar(200) DEFAULT NULL,
  `equipment_type` varchar(100) DEFAULT NULL,
  `work_width` varchar(50) DEFAULT NULL,
  `efficiency` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `price_unit` varchar(20) NOT NULL DEFAULT '亩',
  `min_area` decimal(10,2) DEFAULT NULL,
  `discount_info` varchar(200) DEFAULT NULL,
  `service_area` varchar(255) DEFAULT NULL,
  `main_image` varchar(255) DEFAULT NULL,
  `images` varchar(1000) DEFAULT NULL,
  `is_certified` int DEFAULT 0,
  `is_hot` int DEFAULT 0,
  `is_new` int DEFAULT 0,
  `status` int NOT NULL DEFAULT 1,
  `sales` int NOT NULL DEFAULT 0,
  `rating` decimal(2,1) DEFAULT 0.0,
  `review_count` int NOT NULL DEFAULT 0,
  `favorite_count` int NOT NULL DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`service_id`),
  KEY `idx_provider_id` (`provider_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sales` (`sales`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务商品表';

-- 服务订单表
DROP TABLE IF EXISTS `service_order`;
CREATE TABLE `service_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL,
  `service_id` bigint NOT NULL,
  `farmer_id` bigint NOT NULL,
  `provider_id` bigint NOT NULL,
  `provider_type` varchar(20) NOT NULL,
  `service_name` varchar(100) NOT NULL,
  `service_price` decimal(10,2) NOT NULL,
  `service_area` decimal(10,2) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `final_amount` decimal(10,2) NOT NULL,
  `service_location` varchar(255) DEFAULT NULL,
  `land_location` varchar(255) DEFAULT NULL,
  `crop_type` varchar(50) DEFAULT NULL,
  `expected_time` datetime DEFAULT NULL,
  `order_status` int NOT NULL DEFAULT 0,
  `payment_status` int DEFAULT 0,
  `payment_time` datetime DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_order_status` (`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务订单表';

-- 服务评价表
DROP TABLE IF EXISTS `service_evaluation`;
CREATE TABLE `service_evaluation` (
  `evaluation_id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  `farmer_id` bigint NOT NULL,
  `provider_id` bigint NOT NULL,
  `rating` int NOT NULL,
  `content` varchar(500) DEFAULT NULL,
  `images` varchar(1000) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`evaluation_id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务评价表';

-- 无人机设备表
DROP TABLE IF EXISTS `drone_device`;
CREATE TABLE `drone_device` (
  `device_id` bigint NOT NULL AUTO_INCREMENT,
  `owner_id` bigint NOT NULL,
  `device_name` varchar(100) NOT NULL,
  `device_model` varchar(50) DEFAULT NULL,
  `brand` varchar(100) DEFAULT NULL,
  `serial_number` varchar(100) DEFAULT NULL,
  `picture` varchar(255) DEFAULT NULL,
  `max_load` decimal(5,2) DEFAULT NULL,
  `endurance` int DEFAULT NULL,
  `hourly_rent` decimal(10,2) DEFAULT NULL,
  `daily_rent` decimal(10,2) DEFAULT NULL,
  `status` int NOT NULL DEFAULT 1,
  `rental_status` int NOT NULL DEFAULT 0,
  `flyer_id` bigint DEFAULT NULL,
  `is_deleted` int NOT NULL DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`device_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='无人机设备表';

-- 设备租借记录表
DROP TABLE IF EXISTS `device_rental`;
CREATE TABLE `device_rental` (
  `rental_id` bigint NOT NULL AUTO_INCREMENT,
  `device_id` bigint NOT NULL,
  `owner_id` bigint NOT NULL,
  `flyer_id` bigint NOT NULL,
  `rental_start_time` datetime NOT NULL,
  `rental_end_time` datetime DEFAULT NULL,
  `rental_status` int NOT NULL DEFAULT 1,
  `rental_price` decimal(10,2) DEFAULT NULL,
  `deposit` decimal(10,2) DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `payment_status` int DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`rental_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_flyer_id` (`flyer_id`),
  KEY `idx_rental_status` (`rental_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备租借记录表';

-- 钱包表
DROP TABLE IF EXISTS `wallet`;
CREATE TABLE `wallet` (
  `wallet_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT 0.00,
  `frozen_balance` decimal(10,2) NOT NULL DEFAULT 0.00,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`wallet_id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包表';

-- 轮播图表
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `target_type` varchar(20) DEFAULT NULL,
  `target_link` varchar(255) DEFAULT NULL,
  `sort` int NOT NULL DEFAULT 0,
  `status` int NOT NULL DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

INSERT INTO `banner` VALUES 
(1, '专业无人机喷洒服务', 'https://picsum.photos/1920/600?random=1', 'service', '/service-list', 1, 1, NOW(), NOW()),
(2, '设备租赁优惠中', 'https://picsum.photos/1920/600?random=2', 'equipment', '/equipment-list', 2, 1, NOW(), NOW()),
(3, '成为认证飞手', 'https://picsum.photos/1920/600?random=3', 'link', '/register?role=flyer', 3, 1, NOW(), NOW());

-- 聊天消息表
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` varchar(50) NOT NULL,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `message_type` varchar(20) NOT NULL DEFAULT 'text',
  `content` text,
  `is_read` int NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 通知表
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `notification_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` varchar(500) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `is_read` int NOT NULL DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

SET FOREIGN_KEY_CHECKS = 1;
