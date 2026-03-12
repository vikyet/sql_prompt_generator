-- Retail Banking schema: Branches, Customers, Accounts, Transactions, Loans
-- Order: drop dependents first, then create in FK order

DROP TABLE IF EXISTS `transaction`;
DROP TABLE IF EXISTS loan;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS branch;

-- Branches: physical locations
CREATE TABLE branch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(16) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    address_line1 VARCHAR(255),
    city VARCHAR(64),
    state VARCHAR(64),
    pincode VARCHAR(12),
    phone VARCHAR(24),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_branch_code (code),
    INDEX idx_branch_status (status),
    INDEX idx_branch_city (city)
);

-- Customers: linked to home branch and tier
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_ref VARCHAR(32) NOT NULL UNIQUE,
    branch_id BIGINT NOT NULL,
    tier VARCHAR(20) NOT NULL DEFAULT 'BASIC',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_customer_branch_id (branch_id),
    INDEX idx_customer_status (status),
    INDEX idx_customer_tier (tier),
    INDEX idx_customer_created_at (created_at),
    FOREIGN KEY (branch_id) REFERENCES branch(id)
);

-- Accounts: belong to customer and branch, unique account number
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    account_number VARCHAR(24) NOT NULL UNIQUE,
    type VARCHAR(32) NOT NULL,
    balance DECIMAL(19,4) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    opened_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_account_customer_id (customer_id),
    INDEX idx_account_branch_id (branch_id),
    INDEX idx_account_number (account_number),
    INDEX idx_account_status (status),
    INDEX idx_account_created_at (created_at),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (branch_id) REFERENCES branch(id)
);

-- Transactions: account activity, optional branch where performed
CREATE TABLE `transaction` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    branch_id BIGINT,
    type VARCHAR(32) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    reference_id VARCHAR(64),
    description VARCHAR(255),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_txn_account_id (account_id),
    INDEX idx_txn_branch_id (branch_id),
    INDEX idx_txn_type (type),
    INDEX idx_txn_created_at (created_at),
    INDEX idx_txn_account_created (account_id, created_at),
    FOREIGN KEY (account_id) REFERENCES account(id),
    FOREIGN KEY (branch_id) REFERENCES branch(id)
);

-- Loans: retail loan products per customer and branch
CREATE TABLE loan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    loan_number VARCHAR(24) NOT NULL UNIQUE,
    product_type VARCHAR(32) NOT NULL,
    principal_amount DECIMAL(19,4) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INT NOT NULL,
    emi_amount DECIMAL(19,4) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    disbursement_date DATE,
    closed_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_loan_customer_id (customer_id),
    INDEX idx_loan_branch_id (branch_id),
    INDEX idx_loan_number (loan_number),
    INDEX idx_loan_status (status),
    INDEX idx_loan_product_type (product_type),
    INDEX idx_loan_disbursement (disbursement_date),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (branch_id) REFERENCES branch(id)
);
