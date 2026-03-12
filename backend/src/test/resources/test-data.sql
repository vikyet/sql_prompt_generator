-- Test data for retail banking schema (branch -> customer -> account -> transaction, loan)
INSERT INTO branch (id, code, name, city, state, status, created_at, updated_at) VALUES
(1, 'BR-T1', 'Test Branch', 'Hyderabad', 'Telangana', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO customer (id, customer_ref, branch_id, tier, status, created_at, updated_at) VALUES
(1, 'CUST001', 1, 'GOLD', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'CUST002', 1, 'SILVER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO account (id, customer_id, branch_id, account_number, type, balance, currency, status, opened_at, created_at, updated_at) VALUES
(1, 1, 1, 'ACC1000000001', 'SAVINGS', 10000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 1, 'ACC1000000002', 'CURRENT', 5000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO `transaction` (id, account_id, branch_id, type, amount, currency, created_at) VALUES
(1, 1, 1, 'CREDIT', 1000, 'INR', CURRENT_TIMESTAMP),
(2, 1, 1, 'DEBIT', 500, 'INR', CURRENT_TIMESTAMP);

INSERT INTO loan (id, customer_id, branch_id, loan_number, product_type, principal_amount, interest_rate, tenure_months, emi_amount, status, created_at, updated_at) VALUES
(1, 1, 1, 'LN1000000001', 'PERSONAL', 100000, 12.0, 12, 8885, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
