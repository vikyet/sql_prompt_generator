package com.finance.sqlprompt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Financial transaction for audit and reporting. Indexed for date and account lookups.
 * JPA: @ManyToOne Account and optional Branch; only created_at (no updated_at).
 */
@Entity
@Table(name = "transaction", indexes = {
    @Index(name = "idx_txn_account_id", columnList = "account_id"),
    @Index(name = "idx_txn_branch_id", columnList = "branch_id"),
    @Index(name = "idx_txn_type", columnList = "type"),
    @Index(name = "idx_txn_created_at", columnList = "created_at"),
    @Index(name = "idx_txn_account_created", columnList = "account_id, created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(nullable = false, length = 32)
    private String type; // CREDIT, DEBIT, TRANSFER

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(length = 3)
    private String currency = "INR";

    @Column(name = "reference_id", length = 64)
    private String referenceId;

    @Column(length = 255)
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
