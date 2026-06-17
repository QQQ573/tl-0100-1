CREATE TABLE IF NOT EXISTS product (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'INSURANCE/AGREEMENT/PACKAGE',
    level VARCHAR(50) NOT NULL COMMENT 'BASIC/STANDARD/PREMIUM',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

CREATE TABLE IF NOT EXISTS student (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    id_card VARCHAR(18) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    has_insurance BOOLEAN NOT NULL DEFAULT FALSE,
    insurance_badge_url VARCHAR(500),
    UNIQUE KEY uk_id_card (id_card)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员表';

CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    out_trade_no VARCHAR(64) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PAID/CANCELLED/REFUNDED',
    student_id BIGINT,
    guardian_name VARCHAR(100),
    guardian_id_card VARCHAR(18),
    guardian_phone VARCHAR(20),
    camp_start_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at DATETIME,
    UNIQUE KEY uk_out_trade_no (out_trade_no),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    KEY idx_order_id (order_id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

CREATE TABLE IF NOT EXISTS payment (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    out_trade_no VARCHAR(64) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL COMMENT 'SUCCESS/FAILED',
    pay_time DATETIME,
    callback_content TEXT,
    KEY idx_out_trade_no (out_trade_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';
