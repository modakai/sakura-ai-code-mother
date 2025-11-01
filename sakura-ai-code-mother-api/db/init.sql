CREATE TABLE `user`
(
    `id`            bigint                                  NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_account`  varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
    `user_password` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
    `union_id`      varchar(256) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '微信开放平台id',
    `mp_open_id`    varchar(256) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '公众号openId',
    `user_name`     varchar(256) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '用户昵称',
    `user_avatar`   varchar(1024) COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '用户头像',
    `user_profile`  varchar(512) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '用户简介',
    `user_role`     varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
    `create_time`   datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`     tinyint                                 NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_union_id` (`union_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户';

-- 应用表
CREATE TABLE `app`
(
    `id`            bigint                                  NOT NULL COMMENT 'id',
    `app_name`      varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名称',
    `cover`         varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用封面',
    `init_prompt`   text COLLATE utf8mb4_unicode_ci         NOT NULL COMMENT '应用初始化提示词',
    `code_gen_type` varchar(32) COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '生成应用的类型',
    `deploy_key`    varchar(64) COLLATE utf8mb4_unicode_ci  NULL COMMENT '部署标识',
    `deploy_time`   datetime COLLATE utf8mb4_unicode_ci     NULL COMMENT '部署时间',
    `priority`      int                                     NOT NULL default 0 COMMENT '优先级(99-精选 9999-置顶)',
    `user_id`       bigint                                  NOT NULL COMMENT '用户id',
    `create_time`   datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`     tinyint                                 NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE uk_deploy_key (deploy_key),
    index idx_user_id (user_id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='应用';

-- 对话历史
CREATE TABLE `chat_history`
(
    `id`           bigint   NOT NULL auto_increment COMMENT 'id',
    `app_id`       bigint   NOT NULL COMMENT '应用id',
    `chat_message` text     NOT NULL COMMENT '对话消息',
    `message_type` char(2)  NOT NULL COMMENT '消息类型(u-用户，a-AI消息)',
    `user_id`      bigint   NOT NULL COMMENT '用户id',
    `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`    tinyint  NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    index idx_appid (app_id),
    index idx_create_time (create_time),
    index idx_app_id_create_time (app_id, create_time)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='对话历史';

-- 完整对话历史表(用于加载对话记忆，包含工具调用信息)
create table chat_history_original
(
    id          bigint comment 'id' primary key,
    message     text                               not null comment '消息',
    messageType varchar(2)                        not null comment 'U-用户，A-ai消息，TQ-工具调用请求，TE-工具调用结果',
    appId       bigint                             not null comment '应用id',
    userId      bigint                             not null comment '创建用户id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    INDEX idx_appId (appId),                       -- 提升基于应用的查询性能
    INDEX idx_createTime (createTime),             -- 提升基于时间的查询性能
    INDEX idx_appId_createTime (appId, createTime) -- 游标查询核心索引
) comment '完整对话历史表(用于加载对话记忆，包含工具调用信息)' collate = utf8mb4_unicode_ci;
