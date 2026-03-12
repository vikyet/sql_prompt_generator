package com.finance.sqlprompt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer entity. PII (name, email) must be masked before sending to LLMs—handled at API layer.
 * JPA: @ManyToOne Branch (home branch), @OneToMany Account and Loan.
 */
@Entity
@Table(name = "customer", indexes = {
    @Index(name = "idx_customer_branch_id", columnList = "branch_id"),
    @Index(name = "idx_customer_status", columnList = "status"),
    @Index(name = "idx_customer_tier", columnList = "tier"),
    @Index(name = "idx_customer_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class Customer extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_ref", nullable = false, unique = true, length = 32)
    private String customerRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false, length = 20)
    private String tier = "BASIC"; // BASIC, SILVER, GOLD, PLATINUM

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "customer", cascade = {}, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = {}, fetch = FetchType.LAZY)
    private List<Loan> loans = new ArrayList<>();
}
