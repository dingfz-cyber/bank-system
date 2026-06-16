mysqldump: [Warning] Using a password on the command line interface can be insecure.
Warning: A partial dump from a server that has GTIDs will by default include the GTIDs of all transactions, even those that changed suppressed parts of the database. If you don't want to restore GTIDs, pass --set-gtid-purged=OFF. To make a complete dump, pass --all-databases --triggers --routines --events. 
Warning: A dump from a server that has GTIDs enabled will by default include the GTIDs of all transactions, even those that were executed during its extraction and might not be represented in the dumped data. This might result in an inconsistent data dump. 
In order to ensure a consistent backup of the database, pass --single-transaction or --lock-all-tables or --source-data. 
-- MySQL dump 10.13  Distrib 9.5.0, for Win64 (x86_64)
--
-- Host: localhost    Database: bank_demo
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '0b5907ec-2278-11f1-959b-02500825666b:1-663';

--
-- Table structure for table `apply_info`
--

DROP TABLE IF EXISTS `apply_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `apply_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `user_id` bigint NOT NULL COMMENT '鐢宠?浜?ID',
  `product_id` bigint NOT NULL COMMENT '浜у搧 ID',
  `real_name` varchar(64) NOT NULL COMMENT '濮撳悕',
  `phone` varchar(20) NOT NULL COMMENT '鑱旂郴鍙风爜',
  `apply_type` tinyint NOT NULL COMMENT '鐢宠?绫诲瀷锛?=鍔炲崱 2=璐锋?',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?姝ｅ父 1宸插垹闄',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓浜',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊浜',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态：0=待审核 1=已通过 2=已拒绝',
  `remark` varchar(500) DEFAULT '' COMMENT '审核备注/拒绝原因',
  `approver_id` bigint DEFAULT NULL COMMENT '审批人 ID',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `card_id` bigint DEFAULT NULL COMMENT '贷款放款目标卡ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢宠?琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apply_info`
--

LOCK TABLES `apply_info` WRITE;
/*!40000 ALTER TABLE `apply_info` DISABLE KEYS */;
INSERT INTO `apply_info` VALUES (1,3,1,'测试用户','13800138000',2,0,'','','2026-06-02 13:02:22','2026-06-02 13:02:22',1,'',10,'2026-06-10 23:43:16',NULL),(2,3,4,'测试用户','13800138000',1,0,'','','2026-06-02 13:02:22','2026-06-02 13:02:22',0,'',NULL,NULL,NULL),(3,1,1,'ZhangSan','13800001111',1,0,'','','2026-06-02 14:17:55','2026-06-02 14:17:55',0,'',NULL,NULL,NULL),(4,1,1,'ZhangSan','13800001111',1,1,'','','2026-06-02 14:25:24','2026-06-09 14:32:27',0,'',NULL,NULL,NULL),(5,5,1,'FinalUser','13800001111',2,0,'','','2026-06-02 15:36:25','2026-06-02 15:36:25',0,'',NULL,NULL,NULL),(6,5,1,'FinalUser','13800001111',2,0,'','','2026-06-02 15:39:01','2026-06-02 15:39:01',0,'',NULL,NULL,NULL),(7,6,8,'丁','13087569115',2,0,'','','2026-06-02 16:24:14','2026-06-02 16:24:14',1,'',7,'2026-06-10 20:05:56',NULL),(8,6,4,'丁大帅','13087569115',1,0,'','','2026-06-09 16:13:34','2026-06-09 16:13:34',1,'',1,'2026-06-09 16:13:52',NULL),(9,6,1,'小丁','13087569115',2,0,'','','2026-06-10 13:55:23','2026-06-10 13:55:23',1,'',1,'2026-06-10 13:55:46',NULL),(10,12,5,'李薯条','17792628555',1,0,'','','2026-06-11 14:14:50','2026-06-11 14:14:50',1,'',10,'2026-06-11 14:16:12',NULL),(11,12,5,'李薯条','17792628555',1,0,'','','2026-06-12 15:32:21','2026-06-12 15:32:21',1,'',10,'2026-06-12 15:32:54',NULL),(12,6,18,'小丁','13087569115',1,0,'','','2026-06-12 16:31:52','2026-06-12 16:31:52',1,'',10,'2026-06-12 16:32:06',NULL),(13,12,11,'李薯条','17792628555',2,0,'','','2026-06-16 15:55:43','2026-06-16 15:55:43',1,'',10,'2026-06-16 15:56:02',NULL),(14,12,12,'李薯条','17792628555',2,0,'','','2026-06-16 18:45:44','2026-06-16 18:45:44',1,'',10,'2026-06-16 18:46:07',3);
/*!40000 ALTER TABLE `apply_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_card`
--

DROP TABLE IF EXISTS `bank_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bank_card` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '持卡人 ID',
  `apply_id` bigint DEFAULT NULL COMMENT '关联申请 ID',
  `card_number` varchar(19) NOT NULL COMMENT '卡号（19位）',
  `card_type` tinyint NOT NULL COMMENT '卡类型：1=借记卡 2=信用卡',
  `credit_limit` decimal(12,2) DEFAULT '0.00' COMMENT '信用额度',
  `balance` decimal(12,2) DEFAULT '0.00' COMMENT '余额',
  `cvv` varchar(3) NOT NULL COMMENT 'CVV 安全码',
  `expiry_date` varchar(5) NOT NULL COMMENT '有效期 MM/YY',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0=正常 1=已冻结 2=已注销',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_card_number` (`card_number`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='银行卡表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_card`
--

LOCK TABLES `bank_card` WRITE;
/*!40000 ALTER TABLE `bank_card` DISABLE KEYS */;
INSERT INTO `bank_card` VALUES (1,6,8,'6228099283238151610',2,10000.00,0.00,'659','06/29',0,0,'','','2026-06-09 16:13:52','2026-06-16 08:56:53'),(2,12,10,'6228115857162380089',2,10000.00,4100.00,'111','06/29',0,0,'','','2026-06-11 14:16:12','2026-06-16 08:56:53'),(3,12,11,'6228124957439229399',2,10000.00,54514.00,'104','06/29',0,0,'','','2026-06-12 15:32:54','2026-06-16 08:56:53'),(4,6,12,'6228125312589146612',1,0.00,0.00,'144','06/29',0,0,'','','2026-06-12 16:32:06','2026-06-16 08:56:53');
/*!40000 ALTER TABLE `bank_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_news`
--

DROP TABLE IF EXISTS `bank_news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bank_news` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `summary` varchar(500) DEFAULT '' COMMENT '摘要',
  `content` text COMMENT '正文',
  `status` tinyint(1) DEFAULT '1' COMMENT '发布状态：0=草稿 1=已发布',
  `sort` int DEFAULT '0' COMMENT '排序权重',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `category` varchar(32) DEFAULT '系统公告',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='新闻公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_news`
--

LOCK TABLES `bank_news` WRITE;
/*!40000 ALTER TABLE `bank_news` DISABLE KEYS */;
INSERT INTO `bank_news` VALUES (1,'系统升级维护通知','为提升服务质量，我行将于本周日凌晨进行系统升级维护。','<p>尊敬的客户：</p><p>为提升服务质量，我行将于<strong>2026年6月15日（周日）凌晨2:00-6:00</strong>进行系统升级维护，届时部分线上业务暂停。请您提前做好资金安排。</p>',1,1,0,'','','2026-06-10 23:01:47','2026-06-10 23:01:47','系统公告'),(2,'新版手机银行正式上线','全新界面、更流畅的操作体验，欢迎体验新版手机银行。','<p>经过数月精心打磨，<strong>网银系统 v3.2 版本</strong>正式上线！</p><p>新增功能：</p><ul><li>智能客服机器人</li><li>贷款计算器</li><li>消息通知系统</li><li>5级角色权限体系</li></ul>',1,2,0,'','','2026-06-10 23:01:47','2026-06-10 23:01:47','产品动态'),(3,'2026年第二季度理财产品报告','我行理财产品稳步增长，为您盘点二季度热门理财产品。','<p>2026年第二季度，我行理财产品规模突破<strong>500亿元</strong>大关。</p><p>热门产品：稳健增值一号（年化4.5%）、活期理财宝（年化2.8%）</p>',1,3,0,'','','2026-06-10 23:01:47','2026-06-10 23:01:47','行业新闻'),(4,'关于防范电信诈骗的安全提示','请提高警惕，切勿向陌生人透露银行卡密码和验证码。','<p><strong>安全提示：</strong></p><p>1. 银行工作人员不会索要您的密码或验证码</p><p>2. 请勿点击不明链接</p><p>3. 转账前请核实对方身份</p>',1,4,0,'','','2026-06-10 23:01:47','2026-06-10 23:01:47','系统公告');
/*!40000 ALTER TABLE `bank_news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_product`
--

DROP TABLE IF EXISTS `bank_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bank_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `product_name` varchar(128) NOT NULL COMMENT '浜у搧鍚',
  `product_type` tinyint NOT NULL COMMENT '浜у搧鍒嗙被锛?=涓?汉涓氬姟 2=淇＄敤鍗?3=鍏?徃閲戣瀺 4=鏅?儬閲戣瀺',
  `rate` decimal(5,2) DEFAULT '0.00' COMMENT '骞村寲鍒╃巼',
  `intro` text COMMENT '浜у搧浠嬬粛',
  `img_path` varchar(255) DEFAULT '' COMMENT '铏氭嫙鍥剧墖璺?緞锛屽? card/card01.png',
  `sort` int DEFAULT '0' COMMENT '鎺掑簭鏉冮噸锛堣秺灏忚秺鍓嶏級',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?姝ｅ父 1宸插垹闄',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓浜',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊浜',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `min_amount` decimal(12,2) DEFAULT '0.00' COMMENT '起购金额',
  `term` varchar(64) DEFAULT '' COMMENT '期限说明',
  `risk_level` tinyint DEFAULT '1' COMMENT '风险等级：1=低 2=中 3=高',
  `product_status` tinyint(1) DEFAULT '0' COMMENT '产品状态：0=正常 1=停售',
  `fee_desc` varchar(255) DEFAULT '' COMMENT '手续费说明',
  PRIMARY KEY (`id`),
  KEY `idx_product_type` (`product_type`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浜у搧淇℃伅琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_product`
--

LOCK TABLES `bank_product` WRITE;
/*!40000 ALTER TABLE `bank_product` DISABLE KEYS */;
INSERT INTO `bank_product` VALUES (1,'幸福存单',1,2.75,'定期存款产品，3年期满本息一次性兑付，利率锁定不受市场波动影响，适合稳健型投资者。提前支取按活期计息。','card/pub01.png',1,0,'','','2026-06-02 13:02:16','2026-06-11 11:42:57',1000.00,'3年',1,0,'免手续费'),(2,'灵活理财',1,3.10,'活期理财，1元起投，随存随取灵活方便。7日年化收益每日更新，资金T+0实时到账，适合日常闲置资金管理。','card/pub02.png',2,0,'','','2026-06-02 13:02:16','2026-06-11 11:42:57',1.00,'随时存取',1,0,'免手续费'),(3,'养老保障',1,3.50,'专属养老储蓄计划，每月定额存入享受复利增值。可设定55/60/65岁到期，到期一次性或年金方式领取。','card/pub03.png',3,0,'','','2026-06-02 13:02:16','2026-06-11 11:42:57',5000.00,'5年',1,0,'免手续费'),(4,'标准信用卡',2,0.00,'入门级信用卡，享受56天免息期、消费积分兑换、账单分期等基础权益。信用额度5000-50000元。','pub/card01.png',3,0,'','','2026-06-02 13:02:16','2026-06-12 15:53:34',0.00,'有效期5年',1,0,'首年免年费，次年消费6笔免'),(5,'白金信用卡',2,0.00,'高端信用卡，享机场贵宾厅、高额旅行保险、高尔夫球场预约等尊享权益。信用额度50000-500000元。','pub/card02.png',4,0,'','','2026-06-02 13:02:16','2026-06-12 15:53:34',0.00,'有效期5年',1,0,'年费200元，消费满12万免次年'),(6,'车主信用卡',2,0.00,'车主专属信用卡，加油85折、免费道路救援、违章查询提醒。绑定ETC享高速通行费95折优惠。','pub/card03.png',5,0,'','','2026-06-02 13:02:16','2026-06-12 15:53:34',0.00,'有效期5年',1,0,'年费100元，首年免年费'),(7,'企业贷',3,4.35,'面向中小微企业的流动资金贷款，纯信用无抵押，线上申请快速放款。支持等额本息、先息后本等多种还款方式。','finance/finance01.jpg',1,0,'','','2026-06-02 13:02:16','2026-06-11 11:42:57',100000.00,'1-5年',2,0,'按贷款金额0.5%收取'),(8,'供应链金融',3,3.85,'基于核心企业信用，为其上下游供应商提供应收账款融资服务。降低供应链资金压力，加速资金周转。','finance/finance02.jpg',2,0,'','','2026-06-02 13:02:16','2026-06-11 11:42:57',500000.00,'按订单周期',2,0,'按融资额0.3%收取'),(9,'单位结算卡',3,0.00,'企业日常结算专用卡，支持多级账户管理、批量代发工资、企业网银对接。单笔限额100万元。','finance/finance03.jpg',3,0,'','','2026-06-02 13:02:16','2026-06-11 11:42:57',1.00,'按协议',1,0,'年费500元/户'),(10,'助农贷',4,4.00,'面向农村种养殖户、农民专业合作社的专项贷款。政府财政贴息支持，手续简便，最快3天放款。','loan/loan01.png',1,0,'','','2026-06-02 13:02:16','2026-06-11 11:42:57',5000.00,'1-3年',2,0,'政府贴息，实际利率低至2%'),(11,'创业贷',4,4.50,'支持大学生、退伍军人、返乡青年等群体创业。创业辅导+金融支持一站式服务，前6个月可只还息。','loan/loan02.png',2,0,'','','2026-06-02 13:02:16','2026-06-11 11:42:57',10000.00,'1-5年',2,0,'免评估费、免担保费'),(12,'教育分期',4,0.00,'教育费用分期支付方案，覆盖K12、高等学历、职业技能培训等多场景。0首付，按月分摊。','loan/loan03.png',3,0,'','','2026-06-02 13:02:16','2026-06-11 11:42:57',1000.00,'3-36期',1,0,'手续费率0.5%/期'),(16,'FinalTest',2,3.50,'test','pub/card01.png',10,1,'','','2026-06-02 15:35:02','2026-06-09 14:33:07',0.00,'',1,0,''),(18,'储蓄借记卡',2,0.35,'日常储蓄借记卡，支持存取款、转账、消费、代扣等基础金融服务。活期利率0.35%，免年费，附赠短信提醒服务。','pub/debit01.png',1,0,'','','2026-06-12 15:38:36','2026-06-12 15:53:34',0.00,'无固定期限',1,0,'免年费、免工本费'),(19,'定期储蓄卡',2,2.10,'定期储蓄产品，1年起存，利率2.1%，支持到期自动转存。提前支取按活期计息，适合中长期资金规划。','pub/savings01.png',2,0,'','','2026-06-12 15:53:34','2026-06-12 15:53:34',1000.00,'1年/3年/5年',1,0,'免年费');
/*!40000 ALTER TABLE `bank_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `img_path` varchar(255) NOT NULL COMMENT '铏氭嫙鍥剧墖璺?緞锛屽? banner/banner01.png',
  `jump_route` varchar(255) DEFAULT '' COMMENT '鍓嶇?璺宠浆璺?敱',
  `sort` int DEFAULT '0' COMMENT '鎺掑簭',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?姝ｅ父 1宸插垹闄',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓浜',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊浜',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='杞?挱鍥捐〃';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (1,'banner/banner01.png','/personal',1,0,'','','2026-06-02 13:02:19','2026-06-02 13:02:19'),(2,'banner/banner02.jpg','/credit',2,0,'','','2026-06-02 13:02:19','2026-06-02 15:15:57'),(3,'banner/banner03.png','/company',3,0,'','','2026-06-02 13:02:19','2026-06-02 15:26:15'),(4,'banner/banner03.png','/puhui',4,0,'','','2026-06-02 15:36:25','2026-06-02 15:36:25');
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `family`
--

DROP TABLE IF EXISTS `family`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `family` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `creator_id` bigint NOT NULL,
  `address` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '0',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `account_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `family`
--

LOCK TABLES `family` WRITE;
/*!40000 ALTER TABLE `family` DISABLE KEYS */;
INSERT INTO `family` VALUES (1,6,'西安市雁塔区电子二路18号','13087569115',1,NULL,'2026-06-16 19:08:31','FAM000001'),(2,12,'西安市雁塔区','17792628555',0,NULL,'2026-06-16 19:12:23','FAM000002'),(3,12,'西安市鄠邑区西安石油大学','17792628555',1,NULL,'2026-06-16 19:19:22','FAM000003');
/*!40000 ALTER TABLE `family` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `family_member`
--

DROP TABLE IF EXISTS `family_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `family_member` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `family_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT '成员',
  `status` tinyint DEFAULT '0',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `creator_approved` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_family` (`family_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `family_member`
--

LOCK TABLES `family_member` WRITE;
/*!40000 ALTER TABLE `family_member` DISABLE KEYS */;
INSERT INTO `family_member` VALUES (1,1,6,'户主',1,'2026-06-16 19:08:31',1),(2,3,12,'户主',1,'2026-06-16 19:22:22',1);
/*!40000 ALTER TABLE `family_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_record`
--

DROP TABLE IF EXISTS `login_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `device` varchar(64) DEFAULT NULL,
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_record`
--

LOCK TABLES `login_record` WRITE;
/*!40000 ALTER TABLE `login_record` DISABLE KEYS */;
INSERT INTO `login_record` VALUES (1,6,'13087569115','127.0.0.1','Web','2026-06-10 21:15:12'),(2,7,'sysadmin','127.0.0.1','Web','2026-06-10 21:18:41'),(3,6,'13087569115','127.0.0.1','Web','2026-06-10 21:27:51'),(4,7,'sysadmin','127.0.0.1','Web','2026-06-10 21:35:01'),(5,7,'sysadmin','127.0.0.1','Web','2026-06-10 21:50:09'),(6,6,'13087569115','127.0.0.1','Web','2026-06-10 21:52:33'),(7,7,'sysadmin','127.0.0.1','Web','2026-06-10 21:52:49'),(8,6,'13087569115','127.0.0.1','Web','2026-06-10 21:56:21'),(9,7,'sysadmin','127.0.0.1','Web','2026-06-10 22:51:40'),(10,9,'bizadmin','127.0.0.1','Web','2026-06-10 22:59:06'),(11,6,'13087569115','127.0.0.1','Web','2026-06-10 23:02:33'),(12,7,'sysadmin','127.0.0.1','Web','2026-06-10 23:04:29'),(13,7,'sysadmin','127.0.0.1','Web','2026-06-10 23:04:44'),(14,8,'auditor','127.0.0.1','Web','2026-06-10 23:16:36'),(15,8,'auditor','127.0.0.1','Web','2026-06-10 23:26:58'),(16,7,'sysadmin','127.0.0.1','Web','2026-06-10 23:28:12'),(17,8,'auditor','127.0.0.1','Web','2026-06-10 23:28:35'),(18,9,'bizadmin','127.0.0.1','Web','2026-06-10 23:29:33'),(19,10,'operator','127.0.0.1','Web','2026-06-10 23:29:34'),(20,7,'sysadmin','127.0.0.1','Web','2026-06-10 23:34:40'),(21,8,'auditor','127.0.0.1','Web','2026-06-10 23:35:17'),(22,9,'bizadmin','127.0.0.1','Web','2026-06-10 23:35:41'),(23,10,'operator','127.0.0.1','Web','2026-06-10 23:36:47'),(24,9,'bizadmin','127.0.0.1','Web','2026-06-10 23:43:15'),(25,10,'operator','127.0.0.1','Web','2026-06-10 23:43:16'),(26,10,'operator','127.0.0.1','Web','2026-06-10 23:47:18'),(27,8,'auditor','127.0.0.1','Web','2026-06-10 23:48:13'),(28,10,'operator','127.0.0.1','Web','2026-06-10 23:48:23'),(29,7,'sysadmin','127.0.0.1','Web','2026-06-10 23:48:44'),(30,10,'operator','127.0.0.1','Web','2026-06-10 23:50:45'),(31,7,'sysadmin','127.0.0.1','Web','2026-06-10 23:53:59'),(32,10,'operator','127.0.0.1','Web','2026-06-10 23:59:35'),(33,8,'auditor','127.0.0.1','Web','2026-06-11 00:00:02'),(34,8,'auditor','127.0.0.1','Web','2026-06-11 08:40:05'),(35,10,'operator','127.0.0.1','Web','2026-06-11 08:41:34'),(36,7,'sysadmin','127.0.0.1','Web','2026-06-11 08:41:53'),(37,9,'bizadmin','127.0.0.1','Web','2026-06-11 08:42:28'),(38,6,'13087569115','127.0.0.1','Web','2026-06-11 08:42:41'),(39,10,'operator','127.0.0.1','Web','2026-06-11 10:34:56'),(40,9,'bizadmin','127.0.0.1','Web','2026-06-11 10:43:21'),(41,6,'13087569115','127.0.0.1','Web','2026-06-11 11:40:31'),(42,9,'bizadmin','127.0.0.1','Web','2026-06-11 13:50:28'),(43,9,'bizadmin','127.0.0.1','Web','2026-06-11 13:53:55'),(44,9,'bizadmin','127.0.0.1','Web','2026-06-11 13:55:29'),(45,12,'17792628555','127.0.0.1','Web','2026-06-11 14:14:04'),(46,9,'bizadmin','127.0.0.1','Web','2026-06-11 14:14:57'),(47,8,'auditor','127.0.0.1','Web','2026-06-11 14:15:09'),(48,10,'operator','127.0.0.1','Web','2026-06-11 14:15:47'),(49,12,'17792628555','127.0.0.1','Web','2026-06-11 14:16:22'),(50,6,'13087569115','127.0.0.1','Web','2026-06-11 14:28:47'),(51,6,'13087569115','127.0.0.1','Web','2026-06-11 14:41:52'),(52,6,'13087569115','127.0.0.1','Web','2026-06-11 14:44:24'),(53,6,'13087569115','127.0.0.1','Web','2026-06-11 14:56:32'),(54,6,'13087569115','127.0.0.1','Web','2026-06-11 14:57:04'),(55,6,'13087569115','127.0.0.1','Web','2026-06-11 14:57:18'),(56,6,'13087569115','127.0.0.1','Web','2026-06-11 14:57:38'),(57,12,'17792628555','127.0.0.1','Web','2026-06-11 15:07:42'),(58,6,'13087569115','127.0.0.1','Web','2026-06-11 15:07:58'),(59,6,'13087569115','127.0.0.1','Web','2026-06-11 15:12:40'),(60,6,'13087569115','127.0.0.1','Web','2026-06-11 15:13:15'),(61,12,'17792628555','127.0.0.1','Web','2026-06-11 15:22:39'),(62,6,'13087569115','127.0.0.1','Web','2026-06-11 15:23:28'),(63,12,'17792628555','127.0.0.1','Web','2026-06-11 15:24:34'),(64,12,'17792628555','127.0.0.1','Web','2026-06-11 15:27:17'),(65,6,'13087569115','127.0.0.1','Web','2026-06-11 15:27:36'),(66,12,'17792628555','127.0.0.1','Web','2026-06-11 15:32:04'),(67,12,'17792628555','127.0.0.1','Web','2026-06-11 15:42:50'),(68,12,'17792628555','127.0.0.1','Web','2026-06-12 10:48:58'),(69,8,'auditor','127.0.0.1','Web','2026-06-12 10:53:20'),(70,9,'bizadmin','127.0.0.1','Web','2026-06-12 10:53:28'),(71,7,'sysadmin','127.0.0.1','Web','2026-06-12 10:53:39'),(72,9,'bizadmin','127.0.0.1','Web','2026-06-12 10:54:07'),(73,10,'operator','127.0.0.1','Web','2026-06-12 10:54:42'),(74,10,'operator','127.0.0.1','Web','2026-06-12 13:44:38'),(75,6,'13087569115','127.0.0.1','Web','2026-06-12 15:05:40'),(76,12,'17792628555','127.0.0.1','Web','2026-06-12 15:23:55'),(77,10,'operator','127.0.0.1','Web','2026-06-12 15:32:47'),(78,12,'17792628555','127.0.0.1','Web','2026-06-12 15:33:00'),(79,6,'13087569115','127.0.0.1','Web','2026-06-12 15:36:25'),(80,10,'operator','127.0.0.1','Web','2026-06-12 16:32:01'),(81,6,'13087569115','127.0.0.1','Web','2026-06-12 16:32:10'),(82,10,'operator','127.0.0.1','Web','2026-06-12 16:34:08'),(83,6,'13087569115','127.0.0.1','Web','2026-06-12 16:35:02'),(84,6,'13087569115','127.0.0.1','Web','2026-06-13 18:39:29'),(85,6,'13087569115','127.0.0.1','Web','2026-06-13 19:51:11'),(86,6,'13087569115','127.0.0.1','Web','2026-06-13 19:53:25'),(87,6,'13087569115','127.0.0.1','Web','2026-06-13 19:54:09'),(88,6,'13087569115','127.0.0.1','Web','2026-06-13 19:54:42'),(89,6,'13087569115','127.0.0.1','Web','2026-06-13 19:55:46'),(90,12,'17792628555','127.0.0.1','Web','2026-06-13 19:55:54'),(91,12,'17792628555','127.0.0.1','Web','2026-06-13 19:56:34'),(92,12,'17792628555','127.0.0.1','Web','2026-06-15 10:09:20'),(93,9,'bizadmin','127.0.0.1','Web','2026-06-15 10:21:23'),(94,7,'sysadmin','127.0.0.1','Web','2026-06-15 16:16:04'),(95,9,'bizadmin','127.0.0.1','Web','2026-06-15 17:00:42'),(96,10,'operator','127.0.0.1','Web','2026-06-15 17:01:02'),(97,8,'auditor','127.0.0.1','Web','2026-06-15 17:01:16'),(98,10,'operator','127.0.0.1','Web','2026-06-15 17:03:12'),(99,9,'bizadmin','127.0.0.1','Web','2026-06-15 17:03:20'),(100,10,'operator','127.0.0.1','Web','2026-06-15 17:04:37'),(101,12,'17792628555','127.0.0.1','Web','2026-06-16 09:08:36'),(102,12,'17792628555','127.0.0.1','Web','2026-06-16 13:51:17'),(103,10,'operator','127.0.0.1','Web','2026-06-16 13:52:09'),(104,12,'17792628555','127.0.0.1','Web','2026-06-16 13:52:14'),(105,12,'17792628555','127.0.0.1','Web','2026-06-16 13:52:37'),(106,12,'17792628555','127.0.0.1','Web','2026-06-16 15:24:46'),(107,12,'17792628555','127.0.0.1','Web','2026-06-16 15:55:09'),(108,10,'operator','127.0.0.1','Web','2026-06-16 15:55:50'),(109,12,'17792628555','127.0.0.1','Web','2026-06-16 15:56:08'),(110,12,'17792628555','127.0.0.1','Web','2026-06-16 16:07:23'),(111,12,'17792628555','127.0.0.1','Web','2026-06-16 16:07:33'),(112,6,'13087569115','127.0.0.1','Web','2026-06-16 17:45:03'),(113,9,'bizadmin','127.0.0.1','Web','2026-06-16 17:54:03'),(114,12,'17792628555','127.0.0.1','Web','2026-06-16 17:58:32'),(115,6,'13087569115','127.0.0.1','Web','2026-06-16 18:06:54'),(116,12,'17792628555','127.0.0.1','Web','2026-06-16 18:44:36'),(117,12,'17792628555','127.0.0.1','Web','2026-06-16 18:45:19'),(118,9,'bizadmin','127.0.0.1','Web','2026-06-16 18:45:49'),(119,10,'operator','127.0.0.1','Web','2026-06-16 18:45:56'),(120,12,'17792628555','127.0.0.1','Web','2026-06-16 18:46:13'),(121,10,'operator','127.0.0.1','Web','2026-06-16 19:12:36'),(122,9,'bizadmin','127.0.0.1','Web','2026-06-16 19:12:54'),(123,8,'auditor','127.0.0.1','Web','2026-06-16 19:13:06'),(124,7,'sysadmin','127.0.0.1','Web','2026-06-16 19:13:14'),(125,10,'operator','127.0.0.1','Web','2026-06-16 19:17:59'),(126,12,'17792628555','127.0.0.1','Web','2026-06-16 19:18:09'),(127,10,'operator','127.0.0.1','Web','2026-06-16 19:19:28'),(128,12,'17792628555','127.0.0.1','Web','2026-06-16 19:22:34'),(129,10,'operator','127.0.0.1','Web','2026-06-16 19:31:01'),(130,12,'17792628555','127.0.0.1','Web','2026-06-16 19:39:49'),(131,7,'sysadmin','127.0.0.1','Web','2026-06-16 19:50:14'),(132,7,'sysadmin','127.0.0.1','Web','2026-06-16 19:54:59'),(133,12,'17792628555','127.0.0.1','Web','2026-06-16 19:55:14'),(134,7,'sysadmin','127.0.0.1','Web','2026-06-16 20:10:48');
/*!40000 ALTER TABLE `login_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payee`
--

DROP TABLE IF EXISTS `payee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payee` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `payee_name` varchar(64) DEFAULT '',
  `account_number` varchar(19) NOT NULL,
  `bank_name` varchar(64) DEFAULT '',
  `remark` varchar(255) DEFAULT '',
  `deleted` tinyint(1) DEFAULT '0',
  `create_by` varchar(64) DEFAULT '',
  `update_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payee`
--

LOCK TABLES `payee` WRITE;
/*!40000 ALTER TABLE `payee` DISABLE KEYS */;
/*!40000 ALTER TABLE `payee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_goods`
--

DROP TABLE IF EXISTS `points_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cost` int NOT NULL,
  `image` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `stock` int DEFAULT '0',
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_goods`
--

LOCK TABLES `points_goods` WRITE;
/*!40000 ALTER TABLE `points_goods` DISABLE KEYS */;
INSERT INTO `points_goods` VALUES (1,'手机壳',80,'points/phone_case.png',50,0),(2,'手机支架',60,'points/phone_holder.png',80,0),(3,'银行卡卡包',40,'points/card_holder.png',100,0),(4,'晴雨伞',120,'points/umbrella.png',30,0),(5,'帆布袋',50,'points/tote_bag.png',60,0),(6,'保温杯',100,'points/mug.png',40,0);
/*!40000 ALTER TABLE `points_goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_record`
--

DROP TABLE IF EXISTS `points_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `points` int NOT NULL,
  `reason` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_record`
--

LOCK TABLES `points_record` WRITE;
/*!40000 ALTER TABLE `points_record` DISABLE KEYS */;
INSERT INTO `points_record` VALUES (1,12,50,'注册奖励','2026-06-16 17:51:21'),(2,12,5,'每日登录','2026-06-16 17:51:21'),(3,6,200,'注册+登录奖励','2026-06-16 17:51:21'),(4,12,5,'每日登录','2026-06-16 17:58:32'),(5,6,5,'每日登录','2026-06-16 18:06:53'),(6,12,20,'购买理财产品','2026-06-16 18:29:07'),(7,12,5,'每日登录','2026-06-16 18:44:35'),(8,12,5,'每日登录','2026-06-16 18:45:18'),(9,12,5,'每日登录','2026-06-16 18:46:13'),(10,12,5,'每日登录','2026-06-16 19:18:08'),(11,12,5,'每日登录','2026-06-16 19:22:33'),(12,12,5,'每日登录','2026-06-16 19:39:48'),(13,12,5,'每日登录','2026-06-16 19:55:14');
/*!40000 ALTER TABLE `points_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `menu_name` varchar(64) NOT NULL COMMENT '鑿滃崟鍚',
  `permission` varchar(128) NOT NULL COMMENT '鏉冮檺鏍囪瘑锛屽? product:add',
  `parent_id` bigint DEFAULT '0' COMMENT '鐖惰彍鍗?ID',
  `path` varchar(255) DEFAULT '' COMMENT '鍓嶇?璺?敱璺?緞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑿滃崟鏉冮檺琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'产品管理','product:list',0,'/admin/product','2026-06-02 13:01:53','2026-06-02 13:01:53'),(2,'产品新增','product:add',1,'','2026-06-02 13:01:53','2026-06-02 13:01:53'),(3,'产品编辑','product:update',1,'','2026-06-02 13:01:53','2026-06-02 13:01:53'),(4,'产品删除','product:delete',1,'','2026-06-02 13:01:53','2026-06-02 13:01:53'),(5,'产品恢复','product:recover',1,'','2026-06-02 13:01:53','2026-06-02 13:01:53'),(6,'轮播管理','banner:list',0,'/admin/banner','2026-06-02 13:01:53','2026-06-02 13:01:53'),(7,'轮播新增','banner:add',6,'','2026-06-02 13:01:53','2026-06-02 13:01:53'),(8,'轮播编辑','banner:update',6,'','2026-06-02 13:01:53','2026-06-02 13:01:53'),(9,'轮播删除','banner:delete',6,'','2026-06-02 13:01:53','2026-06-02 13:01:53'),(10,'申请查看','apply:list',0,'/admin/apply','2026-06-02 13:01:53','2026-06-02 13:01:53'),(11,'申请删除','apply:delete',10,'','2026-06-02 13:01:53','2026-06-02 13:01:53'),(12,'申请恢复','apply:recover',10,'','2026-06-02 13:01:53','2026-06-02 13:01:53');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_message`
--

DROP TABLE IF EXISTS `sys_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` text,
  `type` varchar(32) DEFAULT 'system',
  `is_read` tinyint(1) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_message`
--

LOCK TABLES `sys_message` WRITE;
/*!40000 ALTER TABLE `sys_message` DISABLE KEYS */;
INSERT INTO `sys_message` VALUES (1,3,'申请已通过','您的贷款申请已通过','approval',0,'2026-06-10 23:43:16'),(2,12,'申请已通过','您的办卡申请已通过','approval',1,'2026-06-11 14:16:12'),(3,6,'转账成功','您已成功转账 ¥1000 至 6228115857162380089','transaction',1,'2026-06-11 14:57:05'),(4,6,'转账成功','您已成功转账 ¥1000 至 6228115857162380089','transaction',1,'2026-06-11 15:07:31'),(5,12,'申请已通过','您的办卡申请已通过','approval',0,'2026-06-12 15:32:54'),(6,6,'申请已通过','您的办卡申请已通过','approval',1,'2026-06-12 16:32:06'),(7,12,'转账成功','您已成功转账 ¥100 至 6228115857162380089','transaction',0,'2026-06-16 15:32:30'),(8,12,'到账通知','您收到来自 17792628555 的转账 ¥100','transaction',1,'2026-06-16 15:32:30'),(9,12,'申请已通过','您的贷款申请已通过','approval',0,'2026-06-16 15:56:02'),(10,12,'申请已通过','您的贷款申请已通过','approval',0,'2026-06-16 18:46:07');
/*!40000 ALTER TABLE `sys_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `role_name` varchar(64) NOT NULL COMMENT '瑙掕壊鍚',
  `role_code` varchar(64) NOT NULL COMMENT '瑙掕壊缂栫爜锛歋UPER_ADMIN / OPERATOR / USER',
  `description` varchar(255) DEFAULT '' COMMENT '瑙掕壊鎻忚堪',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='瑙掕壊琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'系统运维管理员','SYS_ADMIN','后台账号管理、DB备份恢复','2026-06-02 13:01:47','2026-06-10 19:06:44'),(2,'安全审计员','AUDITOR','全系统日志审计、行为监控','2026-06-02 13:01:47','2026-06-10 19:06:44'),(3,'业务配置管理员','BIZ_ADMIN','产品/轮播/新闻管理、风控配置','2026-06-02 13:01:47','2026-06-10 19:06:44'),(4,'普通运营管理员','OPERATOR','用户管理、申请审核、交易查看','2026-06-10 19:06:44','2026-06-10 19:06:44'),(5,'普通个人用户','USER','前台浏览、个人中心、资金交易','2026-06-10 19:06:44','2026-06-10 19:06:44');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `phone` varchar(20) NOT NULL COMMENT '鎵嬫満鍙凤紙鐧诲綍璐﹀彿锛',
  `password` varchar(255) NOT NULL COMMENT '鍔犲瘑瀛樺偍',
  `nick_name` varchar(64) NOT NULL COMMENT '鏄电О',
  `role_id` bigint NOT NULL DEFAULT '0' COMMENT '瑙掕壊 ID锛?=鏅??鐢ㄦ埛锛',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?姝ｅ父 1宸插垹闄',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓浜',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊浜',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `real_name` varchar(64) DEFAULT '' COMMENT '真实姓名',
  `id_card` varchar(18) DEFAULT '' COMMENT '身份证号',
  `transaction_password` varchar(255) DEFAULT '' COMMENT '交易密码 BCrypt',
  `login_attempts` int DEFAULT '0' COMMENT '连续登录失败次数',
  `locked_at` datetime DEFAULT NULL COMMENT '账号锁定时间',
  `points` int DEFAULT '0' COMMENT '积分',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢ㄦ埛淇℃伅琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (6,'13087569115','$2a$10$KJ8UfM1/1J0RbLkWrjQyF.XCYR113MOHmCPvcNNMvQdmrYExaFoQ.','小丁',5,0,'','','2026-06-02 16:23:17','2026-06-16 18:42:30','丁福珍','330106200001011234','$2a$10$PTfG9vqAP3KLVrsUt3bbY.X1jdQWX2bNgmYKpJkEw7G2DZvcA/xyK',0,NULL,205),(7,'sysadmin','$2b$10$Prsp.2y4ffOJVHrUtqbEbupKl2VnboOUjc3SxW81Z976vOLJzrHHG','运维管理员',1,0,'','','2026-06-10 16:31:44','2026-06-15 16:17:56','','','',0,NULL,0),(8,'auditor','$2b$10$Prsp.2y4ffOJVHrUtqbEbupKl2VnboOUjc3SxW81Z976vOLJzrHHG','审计员老李',2,0,'','','2026-06-10 16:31:44','2026-06-15 16:17:56','','','',0,NULL,0),(9,'bizadmin','$2b$10$Prsp.2y4ffOJVHrUtqbEbupKl2VnboOUjc3SxW81Z976vOLJzrHHG','业务管理员',3,0,'','','2026-06-10 16:31:44','2026-06-15 16:17:56','','','',0,NULL,0),(10,'operator','$2b$10$Prsp.2y4ffOJVHrUtqbEbupKl2VnboOUjc3SxW81Z976vOLJzrHHG','运营小张',4,0,'','','2026-06-10 16:31:44','2026-06-15 16:17:56','','','',0,NULL,0),(11,'13800138000','$2b$10$Prsp.2y4ffOJVHrUtqbEbupKl2VnboOUjc3SxW81Z976vOLJzrHHG','测试用户',5,0,'','','2026-06-10 16:31:44','2026-06-16 18:42:30','','','',0,NULL,0),(12,'17792628555','$2a$10$BpU1A3JbXaMg964jlkYDnuh68uz23wQV/B7zCkQ4oMhTecWgVHOxi','薯条',5,0,'','','2026-06-11 14:13:45','2026-06-16 18:42:30','李薯条','350925209403100026','$2a$10$vWDossbCax2c/t2yeuamzuj3jWQryrwZWIISHUumiVCJJsNDUhMcC',0,NULL,80);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛 ID',
  `role_id` bigint NOT NULL COMMENT '瑙掕壊 ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢ㄦ埛瑙掕壊鍏宠仈琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,1),(2,2,2),(3,3,3);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_record`
--

DROP TABLE IF EXISTS `transaction_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `from_card_id` bigint DEFAULT NULL,
  `to_account` varchar(32) NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `fee` decimal(12,2) DEFAULT '0.00',
  `type` tinyint NOT NULL,
  `status` tinyint DEFAULT '1',
  `remark` varchar(255) DEFAULT '',
  `trade_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  `create_by` varchar(64) DEFAULT '',
  `update_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_trade_time` (`trade_time`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_record`
--

LOCK TABLES `transaction_record` WRITE;
/*!40000 ALTER TABLE `transaction_record` DISABLE KEYS */;
INSERT INTO `transaction_record` VALUES (1,6,1,'6228115857162380089',1000.00,0.00,1,1,'test','2026-06-11 14:57:05',0,'','','2026-06-11 14:57:05','2026-06-11 14:57:05'),(2,6,1,'6228115857162380089',1000.00,0.00,1,1,'测试2','2026-06-11 15:07:31',0,'','','2026-06-11 15:07:31','2026-06-11 15:07:31'),(3,12,1,'6228099283238151610',1000.00,0.00,1,1,'测试补录','2026-06-12 15:26:34',0,'','','2026-06-12 15:26:34','2026-06-12 15:30:29'),(4,12,1,'6228099283238151610',1000.00,0.00,1,1,'转入-补录','2026-06-11 14:57:05',0,'','','2026-06-12 15:29:52','2026-06-12 15:29:52'),(5,12,3,'6228124957439229399',50.00,0.00,5,1,'','2026-06-16 09:15:50',0,'','','2026-06-16 09:15:50','2026-06-16 09:15:50'),(6,12,3,'6228124957439229399',60.00,0.00,6,1,'','2026-06-16 09:18:22',0,'','','2026-06-16 09:18:22','2026-06-16 09:18:22'),(7,12,3,'6228115857162380089',100.00,0.00,1,1,'话费','2026-06-16 15:32:30',0,'','','2026-06-16 15:32:30','2026-06-16 15:32:30'),(8,12,2,'6228124957439229399',100.00,0.00,1,1,'话费','2026-06-16 15:32:30',0,'','','2026-06-16 15:32:30','2026-06-16 15:32:30'),(9,12,NULL,'6228124957439229399',50000.00,0.00,4,1,'贷款放款','2026-06-16 18:46:07',0,'','','2026-06-16 18:46:07','2026-06-16 18:46:07'),(10,12,3,'FAM000003',247.00,0.00,2,1,'water缴费','2026-06-16 20:00:29',0,'','','2026-06-16 20:00:29','2026-06-16 20:00:29'),(11,12,3,'FAM000003',129.00,0.00,2,1,'electric缴费','2026-06-16 20:01:18',0,'','','2026-06-16 20:01:18','2026-06-16 20:01:18');
/*!40000 ALTER TABLE `transaction_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utility_account`
--

DROP TABLE IF EXISTS `utility_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utility_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  `account_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `holder_name` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `address` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `balance` decimal(12,2) DEFAULT '0.00',
  `deleted` tinyint(1) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `family_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account` (`account_no`,`type`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utility_account`
--

LOCK TABLES `utility_account` WRITE;
/*!40000 ALTER TABLE `utility_account` DISABLE KEYS */;
INSERT INTO `utility_account` VALUES (26,6,'water','FAM000001','户主','',0.00,0,'2026-06-16 19:30:59',1),(27,12,'water','FAM000003','户主','',247.00,0,'2026-06-16 19:30:59',3),(29,6,'electric','FAM000001','户主','',0.00,0,'2026-06-16 19:30:59',1),(30,12,'electric','FAM000003','户主','',129.00,0,'2026-06-16 19:30:59',3),(32,6,'gas','FAM000001','户主','',0.00,0,'2026-06-16 19:30:59',1),(33,12,'gas','FAM000003','户主','',0.00,0,'2026-06-16 19:30:59',3),(35,6,'broadband','FAM000001','户主','',0.00,0,'2026-06-16 19:56:46',1),(36,12,'broadband','FAM000003','户主','',0.00,0,'2026-06-16 19:56:46',3);
/*!40000 ALTER TABLE `utility_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wealth_holding`
--

DROP TABLE IF EXISTS `wealth_holding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wealth_holding` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `card_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `product_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  `rate` decimal(5,2) DEFAULT NULL,
  `daily_earn` decimal(12,2) DEFAULT '0.00',
  `status` tinyint DEFAULT '0',
  `buy_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wealth_holding`
--

LOCK TABLES `wealth_holding` WRITE;
/*!40000 ALTER TABLE `wealth_holding` DISABLE KEYS */;
INSERT INTO `wealth_holding` VALUES (1,12,3,1,'幸福存单',1000.00,2.75,0.00,0,'2026-06-16 18:29:07');
/*!40000 ALTER TABLE `wealth_holding` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-16 20:22:59
