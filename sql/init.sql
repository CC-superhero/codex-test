-- ============================================
-- 抢购电商平台 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS flash_sale DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE flash_sale;

-- 1. 用户表
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nickname VARCHAR(64) NOT NULL COMMENT '昵称',
    invite_code VARCHAR(16) NOT NULL UNIQUE COMMENT '本人邀请码',
    parent_invite_code VARCHAR(16) COMMENT '注册时填写的邀请码',
    parent_id BIGINT COMMENT '上级用户ID',
    avatar VARCHAR(255) COMMENT '头像',
    address VARCHAR(255) COMMENT '地址',
    real_name VARCHAR(32) COMMENT '真实姓名',
    phone VARCHAR(16) COMMENT '手机号',
    bank_card VARCHAR(32) COMMENT '银行卡号',
    wechat_qr VARCHAR(255) COMMENT '微信收款码',
    alipay_qr VARCHAR(255) COMMENT '支付宝收款码',
    id_card VARCHAR(18) COMMENT '身份证号',
    points DECIMAL(12,2) DEFAULT 0.00 COMMENT '积分余额',
    status TINYINT DEFAULT 0 COMMENT '0正常 1封禁',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_invite_code (invite_code),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 商品表
CREATE TABLE t_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT '商品名称',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    image VARCHAR(255) COMMENT '商品图片',
    total_stock INT NOT NULL COMMENT '总库存',
    remain_stock INT NOT NULL COMMENT '剩余库存',
    start_time DATETIME COMMENT '抢购开始时间',
    end_time DATETIME COMMENT '抢购结束时间',
    status TINYINT DEFAULT 0 COMMENT '0未开始 1进行中 2已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 3. 用户库存表
CREATE TABLE t_user_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '所属用户',
    product_id BIGINT COMMENT '商品ID',
    product_name VARCHAR(128) COMMENT '商品名称',
    product_image VARCHAR(255) COMMENT '商品图片',
    price DECIMAL(10,2) COMMENT '价格',
    source TINYINT COMMENT '来源:1抢购 2转卖购买',
    status TINYINT DEFAULT 1 COMMENT '1已购买(仓库) 2已上架(卖家仓库)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户库存表';

-- 4. 订单表
CREATE TABLE t_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '购买用户',
    product_id BIGINT COMMENT '商品ID',
    amount DECIMAL(10,2) COMMENT '金额',
    points_used DECIMAL(12,2) DEFAULT 0.00 COMMENT '使用积分',
    coupon_id BIGINT COMMENT '使用的优惠券ID',
    status TINYINT DEFAULT 0 COMMENT '0待支付 1已支付',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 5. 优惠券表
CREATE TABLE t_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '所属用户',
    name VARCHAR(64) COMMENT '券名称',
    face_value DECIMAL(10,2) COMMENT '面值',
    expire_time DATETIME COMMENT '有效期',
    status TINYINT DEFAULT 0 COMMENT '0未使用 1已使用 2已过期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 6. 积分记录表
CREATE TABLE t_points_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    change_amount DECIMAL(12,2) NOT NULL COMMENT '变动(正=收入,负=支出)',
    type TINYINT COMMENT '类型:1抢购转化 2邀请奖励 3手续费返还 4后台发放',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 7. 邀请关系表
CREATE TABLE t_invite_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT NOT NULL COMMENT '上级用户ID',
    child_id BIGINT NOT NULL COMMENT '下级用户ID',
    level INT DEFAULT 1 COMMENT '层级',
    commission_rate DECIMAL(5,4) DEFAULT 0.05 COMMENT '分佣比例',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id),
    INDEX idx_child_id (child_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请关系表';

-- 8. 抢购记录表
CREATE TABLE t_flash_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    amount DECIMAL(10,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抢购记录表';

-- 9. 系统配置表
CREATE TABLE t_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(64) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(255) COMMENT '配置值',
    remark VARCHAR(255) COMMENT '说明'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 默认配置
INSERT INTO t_config (config_key, config_value, remark) VALUES
('flash.points.rate', '1', '抢购积分转化比例(1元=N积分)'),
('invite.commission.rate', '0.05', '邀请分佣比例'),
('listing.fee.rate', '0.02', '上架手续费比例');

-- 管理员表
CREATE TABLE t_admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码(BCrypt)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 测试商品
INSERT INTO t_product (name, price, total_stock, remain_stock, start_time, end_time, status) VALUES
('iPhone 15', 6999.00, 100, 100, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), 0),
('MacBook Pro', 14999.00, 50, 50, DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 0),
('AirPods Pro', 1999.00, 200, 200, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY), 0),
('iPad Air', 4999.00, 80, 80, DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 14 DAY), 0);
