DROP TABLE IF EXISTS `iam`.`sys_user`;
CREATE TABLE `sys_user`
(
    `id`       varchar(50)  NOT NULL,
    `account`  varchar(50)  NOT NULL,
    `username` varchar(50)  NOT NULL,
    `password` varchar(100) NOT NULL,
    `phone`    varchar(20)  NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `username` (`username`) USING BTREE,
    UNIQUE KEY `account` (`account`) USING BTREE,
    UNIQUE KEY `phone` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `iam`.`sys_user_project`;
CREATE TABLE `sys_user_project`
(
    `id`         varchar(50) NOT NULL,
    `user_id`    varchar(50) NOT NULL,
    `project_id` varchar(50) NOT NULL,
    `user_role`  varchar(50) NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `iam`.`sys_role_auth`;
CREATE TABLE `sys_role_auth`
(
    `id`        varchar(50) NOT NULL,
    `role_name` varchar(50) NOT NULL,
    `auth_name` varchar(50) NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `iam`.`sys_project`;
CREATE TABLE `sys_project` (
                               `id` varchar(50) NOT NULL,
                               `title` varchar(200) NOT NULL,
                               `content` varchar(2000) NOT NULL,
                               `attachment` varchar(2000) NOT NULL,
                               `status` tinyint(1) NOT NULL DEFAULT '0',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_role_auth
-- ----------------------------
INSERT INTO `sys_role_auth`
VALUES ('226db75d-a6c0-d412-1890-65f7d58c7c6b', 'leader', 'read');
INSERT INTO `sys_role_auth`
VALUES ('4eba7ce0-f3be-aaca-2d99-f1a6a02653ac', 'leader', 'update');
INSERT INTO `sys_role_auth`
VALUES ('63715183-2637-da42-f08f-3ce4db752026', 'leader', 'delete');
INSERT INTO `sys_role_auth`
VALUES ('8aa7a086-f3d8-1a80-1fb3-822174ae708c', 'leader', 'invite');
INSERT INTO `sys_role_auth`
VALUES ('8c828882-4284-d0df-a3fa-77eaac3493e3', 'teamworker', 'invite');
INSERT INTO `sys_role_auth`
VALUES ('aca64af3-9b34-e01b-b1bd-88984de2ee8f', 'teamworker', 'update');
INSERT INTO `sys_role_auth`
VALUES ('b4889897-8bf0-42e8-7892-39d09c6a09f7', 'leader', 'archive');
INSERT INTO `sys_role_auth`
VALUES ('d877eee8-26d3-65d9-ec46-840af109af85', 'observer', 'read');
INSERT INTO `sys_role_auth`
VALUES ('e0bf6d91-7948-9649-c4ef-492621033d3e', 'teamworker', 'read');