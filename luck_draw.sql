/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50560
 Source Host           : localhost:3306
 Source Schema         : luck_draw

 Target Server Type    : MySQL
 Target Server Version : 50560
 File Encoding         : 65001

 Date: 25/07/2019 15:22:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for luck_recode
-- ----------------------------
DROP TABLE IF EXISTS `luck_recode`;
CREATE TABLE `luck_recode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT NULL,
  `prizeId` int(11) DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for prize
-- ----------------------------
DROP TABLE IF EXISTS `prize`;
CREATE TABLE `prize` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `grade` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `prizeName` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `luckCode` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `balance` int(10) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of prize
-- ----------------------------
BEGIN;
INSERT INTO `prize` VALUES (1, '一等奖', '华为P30', '99999', 1);
INSERT INTO `prize` VALUES (2, '二等奖', 'kindle阅读器', '88888', 3);
INSERT INTO `prize` VALUES (3, '三等奖', '蓝牙耳机', '77777', 5);
INSERT INTO `prize` VALUES (4, '四等奖', '迷你风扇', '66666', 8);
INSERT INTO `prize` VALUES (5, '五等奖', '定制小礼品', '55555', 40);
INSERT INTO `prize` VALUES (6, '幸运奖', '代金券或红包', '44444', 9999);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
