package com.finance.sqlprompt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * BFSI-style account entity. Balance and currency support for portfolio use cases.
 * JPA: @ManyToOne Customer and Branch, @OneToMany Transaction.
 */
@Entity
@Table(name = "account", indexes = {
    @Index(name = "idx_account_customer_id", columnList = "customer_id"),
    @Index(name = "idx_account_branch_id", columnList = "branch_id"),
    @Index(name = "idx_account_number", columnList = "account_number"),
    @Index(name = "idx_account_status", columnList = "status"),
    @Index(name = "idx_account_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class Account extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "account_number", nullable = false, unique = true, length = 24)
    private String accountNumber;

    @Column(nullable = false, length = 32)
    private String type; // SAVINGS, CURRENT, FIXED_DEPOSIT

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String currency = "INR";

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "opened_at", nullable = false, updatable = false)
    private Instant openedAt;

    @OneToMany(mappedBy = "account", cascade = {}, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    @PrePersist
    void setOpenedAt() {
        if (openedAt == null) openedAt = Instant.now();
    }
}
