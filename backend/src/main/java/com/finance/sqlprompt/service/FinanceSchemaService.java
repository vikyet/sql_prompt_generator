package com.finance.sqlprompt.service;

import com.finance.sqlprompt.dto.TableSchemaDto;
import com.finance.sqlprompt.dto.TableSchemaDto.ColumnSchemaDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides table/column metadata so the agent can build correct SQL. Single place to define allowed tables.
 */
@Service
public class FinanceSchemaService {

    private static final List<TableSchemaDto> SCHEMA = List.of(
        new TableSchemaDto(
            "branch",
            "Retail branches: code, name, city, state, address, phone, status",
            List.of(
                new ColumnSchemaDto("id", "BIGINT", "Primary key"),
                new ColumnSchemaDto("code", "VARCHAR(16)", "Unique branch code"),
                new ColumnSchemaDto("name", "VARCHAR(128)", "Branch name"),
                new ColumnSchemaDto("address_line1", "VARCHAR(255)", "Address"),
                new ColumnSchemaDto("city", "VARCHAR(64)", "City"),
                new ColumnSchemaDto("state", "VARCHAR(64)", "State"),
                new ColumnSchemaDto("pincode", "VARCHAR(12)", "Pincode"),
                new ColumnSchemaDto("phone", "VARCHAR(24)", "Phone"),
                new ColumnSchemaDto("status", "VARCHAR(20)", "ACTIVE, INACTIVE"),
                new ColumnSchemaDto("created_at", "TIMESTAMP", "Creation time"),
                new ColumnSchemaDto("updated_at", "TIMESTAMP", "Last update")
            )
        ),
        new TableSchemaDto(
            "customer",
            "Customers linked to home branch; tier: BASIC, SILVER, GOLD, PLATINUM. Prefer customer_ref in reports.",
            List.of(
                new ColumnSchemaDto("id", "BIGINT", "Primary key"),
                new ColumnSchemaDto("customer_ref", "VARCHAR(32)", "External reference (use in reports)"),
                new ColumnSchemaDto("branch_id", "BIGINT", "FK to branch.id (home branch)"),
                new ColumnSchemaDto("tier", "VARCHAR(20)", "BASIC, SILVER, GOLD, PLATINUM"),
                new ColumnSchemaDto("status", "VARCHAR(20)", "ACTIVE, INACTIVE"),
                new ColumnSchemaDto("created_at", "TIMESTAMP", "Creation time"),
                new ColumnSchemaDto("updated_at", "TIMESTAMP", "Last update")
            )
        ),
        new TableSchemaDto(
            "account",
            "Customer accounts: account_number unique, type SAVINGS/CURRENT/FIXED_DEPOSIT, balance, branch_id",
            List.of(
                new ColumnSchemaDto("id", "BIGINT", "Primary key"),
                new ColumnSchemaDto("customer_id", "BIGINT", "FK to customer.id"),
                new ColumnSchemaDto("branch_id", "BIGINT", "FK to branch.id"),
                new ColumnSchemaDto("account_number", "VARCHAR(24)", "Unique account number"),
                new ColumnSchemaDto("type", "VARCHAR(32)", "SAVINGS, CURRENT, FIXED_DEPOSIT"),
                new ColumnSchemaDto("balance", "DECIMAL(19,4)", "Current balance"),
                new ColumnSchemaDto("currency", "VARCHAR(3)", "e.g. INR"),
                new ColumnSchemaDto("status", "VARCHAR(20)", "ACTIVE, CLOSED"),
                new ColumnSchemaDto("opened_at", "TIMESTAMP", "Account open date"),
                new ColumnSchemaDto("created_at", "TIMESTAMP", "Creation time"),
                new ColumnSchemaDto("updated_at", "TIMESTAMP", "Last update")
            )
        ),
        new TableSchemaDto(
            "transaction",
            "Financial transactions: account_id, optional branch_id, type CREDIT/DEBIT/TRANSFER, amount",
            List.of(
                new ColumnSchemaDto("id", "BIGINT", "Primary key"),
                new ColumnSchemaDto("account_id", "BIGINT", "FK to account.id"),
                new ColumnSchemaDto("branch_id", "BIGINT", "FK to branch.id (where txn occurred, nullable)"),
                new ColumnSchemaDto("type", "VARCHAR(32)", "CREDIT, DEBIT, TRANSFER"),
                new ColumnSchemaDto("amount", "DECIMAL(19,4)", "Transaction amount"),
                new ColumnSchemaDto("currency", "VARCHAR(3)", "e.g. INR"),
                new ColumnSchemaDto("reference_id", "VARCHAR(64)", "External reference"),
                new ColumnSchemaDto("description", "VARCHAR(255)", "Optional description"),
                new ColumnSchemaDto("created_at", "TIMESTAMP", "Transaction time")
            )
        ),
        new TableSchemaDto(
            "loan",
            "Retail loans: loan_number unique, product_type PERSONAL/HOME/AUTO/EDUCATION, principal, EMI, status",
            List.of(
                new ColumnSchemaDto("id", "BIGINT", "Primary key"),
                new ColumnSchemaDto("customer_id", "BIGINT", "FK to customer.id"),
                new ColumnSchemaDto("branch_id", "BIGINT", "FK to branch.id"),
                new ColumnSchemaDto("loan_number", "VARCHAR(24)", "Unique loan number"),
                new ColumnSchemaDto("product_type", "VARCHAR(32)", "PERSONAL, HOME, AUTO, EDUCATION"),
                new ColumnSchemaDto("principal_amount", "DECIMAL(19,4)", "Principal"),
                new ColumnSchemaDto("interest_rate", "DECIMAL(5,2)", "Annual interest %"),
                new ColumnSchemaDto("tenure_months", "INT", "Tenure in months"),
                new ColumnSchemaDto("emi_amount", "DECIMAL(19,4)", "EMI amount"),
                new ColumnSchemaDto("status", "VARCHAR(20)", "PENDING, DISBURSED, ACTIVE, CLOSED"),
                new ColumnSchemaDto("disbursement_date", "DATE", "Date disbursed (null if PENDING)"),
                new ColumnSchemaDto("closed_at", "TIMESTAMP", "When closed (null if active)"),
                new ColumnSchemaDto("created_at", "TIMESTAMP", "Creation time"),
                new ColumnSchemaDto("updated_at", "TIMESTAMP", "Last update")
            )
        )
    );

    private final Timer schemaTimer;

    public FinanceSchemaService(MeterRegistry registry) {
        this.schemaTimer = registry.timer("finance.schema.fetch");
    }

    public List<TableSchemaDto> getSchema() {
        return schemaTimer.record(() -> SCHEMA);
    }
}
