# **Project Title**
 
**基于Spring Boot 3.0 Mybatis-plus的GPTweb后台**
 
## Getting Started

  **JDK>=17
  
  **MySql>=8.0
 
### Installing
 
1.本地运行配置maven，jdk并检查版本是否兼容  

2.安装redis  

3.安装mysql8.0 并创建数据库`gpt`  

4.导入sql  

 ```sql
 -- Table structure for announcement  
 
  DROP TABLE IF EXISTS `announcement`;  
  CREATE TABLE `announcement` (
  `id` bigint NOT NULL,
  `title` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '公告内容',
  `sort` int DEFAULT '0' COMMENT '排序',
  `type` int DEFAULT NULL COMMENT '公告类型 1-公告、2-指南',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='公告';  

-- Table structure for gpt_key  

DROP TABLE IF EXISTS `gpt_key`;
CREATE TABLE `gpt_key` (
  `id` bigint NOT NULL,
  `key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'key',
  `use_number` int DEFAULT '0' COMMENT '使用次数',
  `sort` int DEFAULT '0' COMMENT '排序',
  `state` int DEFAULT '0' COMMENT '状态 0 启用 1禁用',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='gptkey\n';  

-- Table structure for pay_config  

DROP TABLE IF EXISTS `pay_config`;
CREATE TABLE `pay_config` (
  `id` bigint NOT NULL,
  `pid` int DEFAULT NULL COMMENT '商户id',
  `secret_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商户密钥',
  `notify_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '回调域名',
  `return_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '跳转通知地址',
  `submit_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付请求域名',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='支付配置';    

-- Table structure for product  

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '产品名',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `type` tinyint DEFAULT '0' COMMENT '类型 0 次数 1 月卡 2 加油包',
  `number_times` int DEFAULT NULL COMMENT '次数',
  `monthly_number` int DEFAULT NULL COMMENT '月卡每日可使用次数',
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

-- Table structure for refueling_kit  

DROP TABLE IF EXISTS `refueling_kit`;
CREATE TABLE `refueling_kit` (
  `id` bigint NOT NULL,
  `product_id` bigint DEFAULT NULL COMMENT '产品id',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `number_times` int DEFAULT NULL COMMENT '可使用次数',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='加油包';  

-- Table structure for t_order  

DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` bigint NOT NULL,
  `product_id` bigint DEFAULT NULL COMMENT '产品id',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `state` int DEFAULT '0' COMMENT '状态 0待支付 1支付完成 2 支付失败',
  `pay_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付方式 wxpay、alipay、qqpay',
  `pay_number` int DEFAULT NULL COMMENT '购买数量',
  `trade_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '易支付订单号',
  `msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付结果消息\n',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='订单表';  

-- Table structure for use_log  

DROP TABLE IF EXISTS `use_log`;
CREATE TABLE `use_log` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `use_number` int DEFAULT '1' COMMENT '使用次数',
  `use_type` tinyint DEFAULT '1' COMMENT '使用类型 1 次数 2 月卡 3加油包',
  `use_value` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '聊天内容',
  `kit_id` bigint DEFAULT NULL COMMENT '加油包id',
  `gpt_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '使用gptkey',
  `state` tinyint DEFAULT '0' COMMENT '是否成功 0成功 1失败',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='使用记录表';  

-- Table structure for user  

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '姓名',
  `mobile` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '1' COMMENT '手机号',
  `password` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '密码',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  `type` tinyint DEFAULT '0' COMMENT '类型 0 次数用户 1 月卡用户 -1 管理员',
  `expiration_time` datetime DEFAULT NULL COMMENT '月卡到期日期',
  `remaining_times` int DEFAULT '0' COMMENT '剩余次数',
  `card_day_max_number` int DEFAULT '0' COMMENT '月卡当日使用最大次数',
  `data_version` int DEFAULT '0' COMMENT '数据版本（默认为0，每次编辑+1）',
  `deleted` int DEFAULT '0' COMMENT '是否删除：0-否、1-是',
  `creator` bigint DEFAULT '0' COMMENT '创建人编号（默认为0）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认为创建时服务器时间）',
  `operator` bigint DEFAULT '0' COMMENT '操作人编号（默认为0）',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间（每次更新时自动更新）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户表';  

5.修改yml种的mysql与redis连接地址与账号密码
  dev-开发环境
  prod-生产环境  
  
```java
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://127.0.0.1/gpt?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&autoReconnect=true&failOverReadOnly=false
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    filters: stat
    maxActive: 20
    initialSize: 1
    maxWait: 60000
    minIdle: 1
    timeBetweenEvictionRunsMillis: 60000
    minEvictableIdleTimeMillis: 300000
    validationQuery: select 'x'
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    poolPreparedStatements: true
    maxOpenPreparedStatements: 20
  data:
    redis:
      host: 127.0.0.1
      port: 6380
      password: password
      database: 0
      jedis:
        pool:
          max-idle: 100
          min-idle: 1
          max-active: 1000
          max-wait: -1
          
```         
## Running the tests
 
启动idea出现如下信息说明运行成功
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8000 (http) with context path ''
com.cn.app.chatgptbot.Application        : Started Application in 5.138 seconds (process running for 5.521)
 
### And coding style tests
 
**后端基于另一个开源项目开发，所用到jdk17特性 
 
## Express gratitude
 
感谢开原作者 [dulaiduwang003](https://github.com/dulaiduwang003/ChatGPT_wechat) 提供的基础GPT功能
 
## Put it last
解释一下pay_config内容，如果自己对接其他支付可忽略
系统对接其他支付，需要自己注册账号密码登录查看相关配置
支付网站地址：https://www.11zhifu.cn/
![image](https://user-images.githubusercontent.com/43660702/228098543-03e82704-92a6-461e-ae7d-fe30796da435.png)
此图片与表对应关系
接口地址：submit_url
商户Id：pid
商户秘钥：secret_key
其他描述
notify_url：支付成功后回调地址
return_url：支付成功后跳转页面
