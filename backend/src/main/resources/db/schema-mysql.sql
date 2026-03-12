-- Finance DB schema for MySQL (run once to create tables; use ddl-auto=validate in production)
CREATE DATABASE IF NOT EXISTS finance_db;
USE finance_db;

CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_ref VARCHAR(32) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_customer_status (status),
    INDEX idx_customer_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    type VARCHAR(32) NOT NULL,
    balance DECIMAL(19,4) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_account_customer_id (customer_id),
    INDEX idx_account_status (status),
    INDEX idx_account_created_at (created_at),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE IF NOT EXISTS `transaction` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    type VARCHAR(32) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    reference_id VARCHAR(64),
    description VARCHAR(255),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_txn_account_id (account_id),
    INDEX idx_txn_type (type),
    INDEX idx_txn_created_at (created_at),
    INDEX idx_txn_account_created (account_id, created_at),
    FOREIGN KEY (account_id) REFERENCES account(id)
);

-- Optional: seed minimal data for testing the agent
INSERT INTO customer (customer_ref, status) VALUES ('CUST001', 'ACTIVE'), ('CUST002', 'ACTIVE')
ON DUPLICATE KEY UPDATE customer_ref = customer_ref;
INSERT INTO account (customer_id, type, balance, currency, status)
SELECT id, 'SAVINGS', 10000.0000, 'INR', 'ACTIVE' FROM customer WHERE customer_ref = 'CUST001' LIMIT 1
ON DUPLICATE KEY UPDATE balance = balance;
INSERT INTO account (customer_id, type, balance, currency, status)
SELECT id, 'CURRENT', 5000.0000, 'INR', 'ACTIVE' FROM customer WHERE customer_ref = 'CUST002' LIMIT 1
ON DUPLICATE KEY UPDATE balance = balance;
