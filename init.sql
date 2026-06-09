-- =============================================
-- 网银项目 bank_demo 数据库建表脚本
-- MySQL 9.6, 字符集 utf8mb4
-- =============================================

USE bank_demo;

-- 1. 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_name   VARCHAR(64)  NOT NULL                COMMENT '角色名',
    role_code   VARCHAR(64)  NOT NULL                COMMENT '角色编码：SUPER_ADMIN / OPERATOR / USER',
    description VARCHAR(255) DEFAULT ''              COMMENT '角色描述',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 菜单权限表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    menu_name   VARCHAR(64)  NOT NULL                COMMENT '菜单名',
    permission  VARCHAR(128) NOT NULL                COMMENT '权限标识，如 product:add',
    parent_id   BIGINT       DEFAULT 0               COMMENT '父菜单 ID',
    path        VARCHAR(255) DEFAULT ''              COMMENT '前端路由路径',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 3. 用户信息表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    phone       VARCHAR(20)  NOT NULL                COMMENT '手机号（登录账号）',
    password    VARCHAR(255) NOT NULL                COMMENT '加密存储',
    nick_name   VARCHAR(64)  NOT NULL                COMMENT '昵称',
    role_id     BIGINT       NOT NULL DEFAULT 0      COMMENT '角色 ID（0=普通用户）',
    deleted     TINYINT(1)   DEFAULT 0               COMMENT '逻辑删除：0正常 1已删除',
    create_by   VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    update_by   VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_phone (phone),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 4. 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id       BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id  BIGINT NOT NULL                COMMENT '用户 ID',
    role_id  BIGINT NOT NULL                COMMENT '角色 ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 5. 产品信息表
DROP TABLE IF EXISTS bank_product;
CREATE TABLE bank_product (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    product_name VARCHAR(128) NOT NULL                COMMENT '产品名',
    product_type TINYINT(2)   NOT NULL                COMMENT '产品分类：1=个人业务 2=信用卡 3=公司金融 4=普惠金融',
    rate         DECIMAL(5,2) DEFAULT 0.00            COMMENT '年化利率',
    intro        TEXT                                  COMMENT '产品介绍',
    img_path     VARCHAR(255) DEFAULT ''              COMMENT '虚拟图片路径，如 card/card01.png',
    sort         INT          DEFAULT 0               COMMENT '排序权重（越小越前）',
    deleted      TINYINT(1)   DEFAULT 0               COMMENT '逻辑删除：0正常 1已删除',
    create_by    VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    update_by    VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_product_type (product_type),
    KEY idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品信息表';

-- 6. 轮播图表
DROP TABLE IF EXISTS banner;
CREATE TABLE banner (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    img_path   VARCHAR(255) NOT NULL                COMMENT '虚拟图片路径，如 banner/banner01.png',
    jump_route VARCHAR(255) DEFAULT ''              COMMENT '前端跳转路由',
    sort       INT          DEFAULT 0               COMMENT '排序',
    deleted    TINYINT(1)   DEFAULT 0               COMMENT '逻辑删除：0正常 1已删除',
    create_by  VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    update_by  VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 7. 申请表
DROP TABLE IF EXISTS apply_info;
CREATE TABLE apply_info (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT       NOT NULL                COMMENT '申请人 ID',
    product_id  BIGINT       NOT NULL                COMMENT '产品 ID',
    real_name   VARCHAR(64)  NOT NULL                COMMENT '姓名',
    phone       VARCHAR(20)  NOT NULL                COMMENT '联系号码',
    apply_type  TINYINT(2)   NOT NULL                COMMENT '申请类型：1=办卡 2=贷款',
    deleted     TINYINT(1)   DEFAULT 0               COMMENT '逻辑删除：0正常 1已删除',
    create_by   VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    update_by   VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请表';
