/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : exam_system

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-07-24 20:03:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `username` varchar(100) DEFAULT NULL COMMENT '登录账号',
  `realname` varchar(100) DEFAULT NULL COMMENT '真实姓名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `salt` varchar(45) DEFAULT NULL COMMENT 'md5密码盐',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `sex` int(11) DEFAULT NULL COMMENT '性别（1：男 2：女）',
  `email` varchar(45) DEFAULT NULL COMMENT '电子邮件',
  `phone` varchar(45) DEFAULT NULL COMMENT '电话',
  `org_code` varchar(100) DEFAULT NULL COMMENT '部门code',
  `idcard` varchar(20) DEFAULT NULL COMMENT '身份证号码',
  `provcode` varchar(6) DEFAULT NULL COMMENT '省编码',
  `citycode` varchar(6) DEFAULT NULL COMMENT '市编码',
  `orgname` varchar(30) DEFAULT NULL COMMENT '机构名称',
  `orgtype` varchar(10) DEFAULT NULL COMMENT '机构类别',
  `jobtype` varchar(10) DEFAULT NULL COMMENT '工作类别',
  `proftitle` varchar(10) DEFAULT NULL COMMENT '职称',
  `status` int(2) DEFAULT NULL COMMENT '状态(1：正常  2：冻结 ）',
  `del_flag` varchar(1) DEFAULT NULL COMMENT '删除状态（0，正常，1已删除）',
  `activiti_sync` varchar(6) DEFAULT NULL COMMENT '同步工作流引擎1同步0不同步',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_user_name` (`username`) USING BTREE,
  KEY `index_user_status` (`status`) USING BTREE,
  KEY `index_user_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('214cd4c612ffc575f9e02887b766dbe8', '12345674', null, '373733b591edb10ab4a2f6657c603a4b', 'i4ZdG76G', null, null, null, null, '12345674', null, null, null, null, null, null, null, null, '1', '0', '1', 'jeecg', '2019-07-24 18:12:05', null, null);
INSERT INTO `sys_user` VALUES ('42d153bffeea74f72a9c1697874fa4a7', 'test22', '23232', 'ac52e15671a377cf', '5FMD48RM', 'user/20190314/ly-plate-e_1552531617500.png', '2019-02-09 00:00:00', '1', 'zhangdaiscott@163.com', '18611782222', null, null, null, null, null, null, null, null, '1', '0', '1', 'admin', '2019-01-26 18:01:10', 'admin', '2019-03-23 15:05:50');
INSERT INTO `sys_user` VALUES ('5682bac3ff220532c03592ae1947dc07', '13810924936', '张三', 'bc69f583412da23e55a9c7e9094062cf', 'BVCb561R', null, null, null, 'wzl@qq.com', '13810924936', null, '372526198108045079', '370000', '372500', '人民医院', '医院', '医生', '中级', '1', '0', null, 'jeecg', '2019-07-24 19:56:53', null, null);
INSERT INTO `sys_user` VALUES ('a75d45a015c44384a04449ee80dc3503', 'jeecg', 'jeecg', '3dd8371f3cf8240e', 'vDDkDzrK', 'user/20190220/e1fe9925bc315c60addea1b98eb1cb1349547719_1550656892940.jpg', null, '2', null, null, null, null, null, null, null, null, null, null, '1', '0', '1', 'admin', '2019-02-13 16:02:36', 'admin', '2019-04-09 15:47:36');
INSERT INTO `sys_user` VALUES ('cf18aa32dd1e0426d8de38a35e4c72ba', '1234567', null, '97f78b721574fffe', 'PSG00vmZ', null, null, null, null, '1234567', null, null, null, null, null, null, null, null, '1', '0', '1', 'jeecg', '2019-07-24 18:10:59', null, null);
INSERT INTO `sys_user` VALUES ('e9ca23d68d884d4ebb19d07889727dae', 'admin', '管理员', 'cb362cfeefbf3d8d', 'RCGTeGiH', 'user/20190119/logo-2_1547868176839.png', '2018-12-05 00:00:00', '1', '11@qq.com', '18566666661', 'A01', null, null, null, null, null, null, null, '1', '0', '1', null, '2038-06-21 17:54:10', 'admin', '2019-05-20 15:59:56');
INSERT INTO `sys_user` VALUES ('f0019fdebedb443c98dcb17d88222c38', 'zhagnxiao', '张小红', 'f898134e5e52ae11a2ffb2c3b57a4e90', 'go3jJ4zX', 'user/20190401/20180607175028Fn1Lq7zw_1554118444672.png', '2019-04-01 00:00:00', null, null, null, null, null, null, null, null, null, null, null, '1', '0', '1', 'admin', '2023-10-01 19:34:10', 'admin', '2019-04-10 22:00:22');
