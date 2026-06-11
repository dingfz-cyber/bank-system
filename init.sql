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
    password    VARCHAR(255) NOT NULL                COMMENT '登录密码 BCrypt',
    nick_name   VARCHAR(64)  NOT NULL                COMMENT '昵称',
    real_name   VARCHAR(64)  DEFAULT ''              COMMENT '真实姓名',
    id_card     VARCHAR(18)  DEFAULT ''              COMMENT '身份证号',
    transaction_password VARCHAR(255) DEFAULT ''     COMMENT '交易密码 BCrypt',
    login_attempts INT       DEFAULT 0               COMMENT '连续登录失败次数',
    locked_at   DATETIME     DEFAULT NULL            COMMENT '账号锁定时间',
    role_id     BIGINT       NOT NULL DEFAULT 5      COMMENT '角色 ID（5=普通用户）',
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
    min_amount   DECIMAL(12,2) DEFAULT 0.00           COMMENT '起购金额',
    term         VARCHAR(64)  DEFAULT ''              COMMENT '期限说明',
    risk_level   TINYINT(2)   DEFAULT 1               COMMENT '风险等级：1=低 2=中 3=高',
    product_status TINYINT(1) DEFAULT 0               COMMENT '产品状态：0=正常 1=停售',
    fee_desc     VARCHAR(255) DEFAULT ''              COMMENT '手续费说明',
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
    status      TINYINT(2)   NOT NULL DEFAULT 0      COMMENT '审核状态：0=待审核 1=已通过 2=已拒绝',
    remark      VARCHAR(500) DEFAULT ''              COMMENT '审核备注/拒绝原因',
    approver_id BIGINT       DEFAULT NULL            COMMENT '审批人 ID',
    approve_time DATETIME    DEFAULT NULL            COMMENT '审批时间',
    deleted     TINYINT(1)   DEFAULT 0               COMMENT '逻辑删除：0正常 1已删除',
    create_by   VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    update_by   VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_product_id (product_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请表';

-- 8. 新闻公告表
DROP TABLE IF EXISTS bank_news;
CREATE TABLE bank_news (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    title       VARCHAR(255) NOT NULL                COMMENT '标题',
    summary     VARCHAR(500) DEFAULT ''              COMMENT '摘要',
    content     TEXT                                  COMMENT '正文',
    status      TINYINT(1)   DEFAULT 1               COMMENT '发布状态：0=草稿 1=已发布',
    sort        INT          DEFAULT 0               COMMENT '排序权重',
    deleted     TINYINT(1)   DEFAULT 0               COMMENT '逻辑删除：0正常 1已删除',
    create_by   VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    update_by   VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻公告表';

-- 9. 银行卡表
DROP TABLE IF EXISTS bank_card;
CREATE TABLE bank_card (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id      BIGINT       NOT NULL                COMMENT '持卡人 ID',
    apply_id     BIGINT       DEFAULT NULL            COMMENT '关联申请 ID',
    card_number  VARCHAR(19)  NOT NULL                COMMENT '卡号（19位）',
    card_type    TINYINT(2)   NOT NULL                COMMENT '卡类型：1=借记卡 2=信用卡',
    credit_limit DECIMAL(12,2) DEFAULT 0.00          COMMENT '信用额度',
    balance      DECIMAL(12,2) DEFAULT 0.00          COMMENT '余额',
    cvv          VARCHAR(3)   NOT NULL                COMMENT 'CVV 安全码',
    expiry_date  VARCHAR(5)   NOT NULL                COMMENT '有效期 MM/YY',
    status       TINYINT(2)   NOT NULL DEFAULT 0     COMMENT '状态：0=正常 1=已冻结 2=已注销',
    deleted     TINYINT(1)   DEFAULT 0               COMMENT '逻辑删除：0正常 1已删除',
    create_by   VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    update_by   VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_card_number (card_number),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='银行卡表';

-- 10. 交易流水表
DROP TABLE IF EXISTS transaction_record;
CREATE TABLE transaction_record (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id       BIGINT       NOT NULL                COMMENT '用户 ID',
    from_card_id  BIGINT       DEFAULT NULL            COMMENT '付款卡 ID',
    to_account    VARCHAR(32)  NOT NULL                COMMENT '收款账号/卡号',
    amount        DECIMAL(12,2) NOT NULL               COMMENT '交易金额',
    fee           DECIMAL(12,2) DEFAULT 0.00          COMMENT '手续费',
    type          TINYINT(2)   NOT NULL                COMMENT '交易类型：1=转账 2=缴费 3=理财 4=还款',
    status        TINYINT(2)   DEFAULT 1              COMMENT '状态：0=失败 1=成功',
    remark        VARCHAR(255) DEFAULT ''             COMMENT '备注',
    trade_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    deleted      TINYINT(1)   DEFAULT 0               COMMENT '逻辑删除',
    create_by    VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    update_by    VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_trade_time (trade_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';
