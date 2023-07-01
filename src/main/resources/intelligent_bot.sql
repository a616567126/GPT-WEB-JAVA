/*
 Navicat Premium Data Transfer

 Source Server         : app
 Source Server Type    : MySQL
 Source Server Version : 80024
 Source Host           : 103.106.189.148:3307
 Source Schema         : intelligent_bot

 Target Server Type    : MySQL
 Target Server Version : 80024
 File Encoding         : 65001

 Date: 01/07/2023 17:37:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL,
  `title` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '公告内容',
  `sort` int DEFAULT '0' COMMENT '排序',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='公告';

-- ----------------------------
-- Table structure for card_pin
-- ----------------------------
DROP TABLE IF EXISTS `card_pin`;
CREATE TABLE `card_pin` (
  `id` bigint NOT NULL,
  `card_pin` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '卡密',
  `number` int DEFAULT NULL COMMENT '次数',
  `user_id` bigint DEFAULT '0' COMMENT '使用用户id',
  `state` tinyint DEFAULT '0' COMMENT '状态 0 未使用 1使用',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='卡密表';

-- ----------------------------
-- Table structure for email_config
-- ----------------------------
DROP TABLE IF EXISTS `email_config`;
CREATE TABLE `email_config` (
  `id` bigint NOT NULL,
  `host` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮件提供商地址',
  `port` int DEFAULT NULL COMMENT '端口号',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮件账号',
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'SMTP授权密码',
  `protocol` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮件协议',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='微信日志';

-- ----------------------------
-- Table structure for error_message
-- ----------------------------
DROP TABLE IF EXISTS `error_message`;
CREATE TABLE `error_message` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `error_message` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '异常内容',
  `url` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '接口地址',
  `position` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '异常位置',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='使用记录表';

-- ----------------------------
-- Table structure for gpt_key
-- ----------------------------
DROP TABLE IF EXISTS `gpt_key`;
CREATE TABLE `gpt_key` (
  `id` bigint NOT NULL,
  `key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'key',
  `use_number` int DEFAULT '0' COMMENT '使用次数',
  `sort` int DEFAULT '0' COMMENT '排序',
  `state` int DEFAULT '0' COMMENT '状态 0 启用 1禁用',
  `type` tinyint DEFAULT '3' COMMENT 'key类型 3-gpt3.5 4-gpt4',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='gptkey\n';

-- ----------------------------
-- Table structure for message_log
-- ----------------------------
DROP TABLE IF EXISTS `message_log`;
CREATE TABLE `message_log` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `use_number` int DEFAULT '1' COMMENT '使用次数',
  `use_type` tinyint DEFAULT '1' COMMENT '消费类型 1 次数 2 月卡',
  `use_value` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '聊天内容',
  `gpt_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '使用gptkey',
  `send_type` tinyint DEFAULT '0' COMMENT '1-gpt对话 2-gpt画图 3-sd画图 4-fs画图 5-mj画图 6-bing 7-stableStudio 8-gpt4\n',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='使用记录表';

-- ----------------------------
-- Table structure for mj_task
-- ----------------------------
DROP TABLE IF EXISTS `mj_task`;
CREATE TABLE `mj_task` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `action` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '1' COMMENT '任务类型',
  `prompt` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关键字',
  `prompt_en` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '译文',
  `description` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '任务描述',
  `state` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '自定义参数',
  `index` int DEFAULT NULL COMMENT '图片位置',
  `status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '任务状态',
  `image_url` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '图片地址',
  `start_time` bigint DEFAULT NULL COMMENT '任务开始时间',
  `submit_time` bigint DEFAULT NULL COMMENT '任务提交时间\n',
  `finish_time` bigint DEFAULT NULL COMMENT '任务完成时间',
  `fail_reason` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '任务失败原因',
  `final_prompt` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'mj 任务信息',
  `notify_hook` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '回调地址',
  `related_task_id` bigint DEFAULT NULL COMMENT '任务关联 id',
  `message_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '消息 id',
  `message_hash` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '消息 hash',
  `progress` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '任务进度',
  `sub_type` tinyint DEFAULT '1' COMMENT '提交类型 1：web 2：公众号',
  `progress_message_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `flags` int DEFAULT NULL,
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='Mj任务';

-- ----------------------------
-- Table structure for pay_config
-- ----------------------------
DROP TABLE IF EXISTS `pay_config`;
CREATE TABLE `pay_config` (
  `id` bigint NOT NULL,
  `pid` int DEFAULT NULL COMMENT '易支付商户id',
  `secret_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '易支付商户密钥',
  `notify_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '易支付回调域名',
  `return_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '易支付跳转通知地址',
  `submit_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '易支付支付请求域名',
  `api_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '易支付订单查询api',
  `pay_type` tinyint DEFAULT '0' COMMENT '支付类型 0 易支付 1卡密',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='支付配置';

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '产品名',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `number_times` int DEFAULT NULL COMMENT '次数',
  `sort` int DEFAULT '0' COMMENT '排序',
  `stock` int DEFAULT '1' COMMENT '库存数',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='产品表';

-- ----------------------------
-- Table structure for sd_lora
-- ----------------------------
DROP TABLE IF EXISTS `sd_lora`;
CREATE TABLE `sd_lora` (
  `id` bigint NOT NULL,
  `lora_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'lora名',
  `img_url` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'lora图片地址',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='sdlora表';

-- ----------------------------
-- Table structure for sd_model
-- ----------------------------
DROP TABLE IF EXISTS `sd_model`;
CREATE TABLE `sd_model` (
  `id` bigint NOT NULL,
  `model_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '模型名',
  `img_url` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '模型图片地址',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='Sd模型表';

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL,
  `registration_method` tinyint DEFAULT '1' COMMENT '注册模式 1账号密码  2 短信注册 3 关闭注册 4邮件注册',
  `default_times` int DEFAULT '10' COMMENT '默认注册次数',
  `gpt_url` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'gpt请求地址',
  `img_upload_url` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '图片上传地址',
  `img_return_url` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '图片域名前缀',
  `api_url` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '后台接口地址',
  `client_url` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '客户端页面地址',
  `is_open_sd` tinyint DEFAULT '0' COMMENT '是否开启sd 0未开启 1开启',
  `sd_url` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'Sd接口地址',
  `sd_lora_url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'sd lora地址',
  `is_open_flag_studio` tinyint DEFAULT '0' COMMENT '是否开启FlagStudio 0-未开启 1开启',
  `flag_studio_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'FlagStudio key',
  `flag_studio_url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'FlagStudio 接口地址',
  `baidu_appid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '百度appid',
  `baidu_secret` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '百度Secret\n',
  `baidu_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '百度应用key',
  `baidu_secret_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '百度应用Secret',
  `is_open_mj` tinyint DEFAULT '0' COMMENT '是否开启mj 0未开启 1开启',
  `mj_guild_id` bigint DEFAULT NULL COMMENT 'Mj服务器id',
  `mj_channel_id` bigint DEFAULT NULL COMMENT 'Mj频道id',
  `mj_user_token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'discordtoken',
  `mj_bot_token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '频道机器人token',
  `mj_bot_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '频道机器人名称',
  `is_open_proxy` tinyint DEFAULT '0' COMMENT '是否开启代理 0关闭 1开启',
  `proxy_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '代理ip',
  `proxy_port` int DEFAULT NULL COMMENT '代理端口',
  `is_open_bing` tinyint DEFAULT '0' COMMENT '是否开启bing 0-未开启 1开启',
  `bing_cookie` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '微软bing cookie',
  `is_open_stable_studio` tinyint DEFAULT '0' COMMENT '是否开启StableStudio 0未开启 1 开启',
  `stable_studio_api` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'StableStudioapi地址前缀',
  `stable_studio_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'StableStudio key',
  `client_logo` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '客户端 logo 地址',
  `client_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '客户端名称',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='系统配置';

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` bigint NOT NULL,
  `product_id` bigint DEFAULT NULL COMMENT '产品id',
  `user_id` bigint DEFAULT NULL COMMENT '购买人id',
  `price` decimal(10,2) DEFAULT NULL COMMENT '订单金额',
  `pay_number` int DEFAULT '0' COMMENT '购买数量',
  `state` int DEFAULT '0' COMMENT '状态 0待支付 1支付完成 2 支付失败',
  `pay_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付方式',
  `trade_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '平台订单号、卡密',
  `msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付消息',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='订单表';

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '姓名',
  `mobile` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '手机号',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '123456' COMMENT '密码',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  `type` tinyint DEFAULT '0' COMMENT '类型 0 次数用户 1 月卡用户 -1 管理员',
  `remaining_times` int DEFAULT '5' COMMENT '剩余次数',
  `from_user_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '微信用户账号',
  `is_event` tinyint DEFAULT '0' COMMENT '是否关注公众号 0未关注 1关注',
  `email` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'email地址',
  `ip_address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户ip地址',
  `browser_fingerprint` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '浏览器指纹',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '头像地址',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户表';

-- ----------------------------
-- Table structure for wx_log
-- ----------------------------
DROP TABLE IF EXISTS `wx_log`;
CREATE TABLE `wx_log` (
  `id` bigint NOT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '请求内容',
  `from_user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '微信用户账号',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='微信日志';

SET FOREIGN_KEY_CHECKS = 1;
