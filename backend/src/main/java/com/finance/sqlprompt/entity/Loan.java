package com.finance.sqlprompt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Retail loan: HOME, PERSONAL, AUTO, EDUCATION. Tracks principal, EMI, status, disbursement.
 * JPA: @ManyToOne Customer and Branch.
 */
@Entity
@Table(name = "loan", indexes = {
    @Index(name = "idx_loan_customer_id", columnList = "customer_id"),
    @Index(name = "idx_loan_branch_id", columnList = "branch_id"),
    @Index(name = "idx_loan_number", columnList = "loan_number"),
    @Index(name = "idx_loan_status", columnList = "status"),
    @Index(name = "idx_loan_product_type", columnList = "product_type"),
    @Index(name = "idx_loan_disbursement", columnList = "disbursement_date")
})
@Getter
@Setter
@NoArgsConstructor
public class Loan extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "loan_number", nullable = false, unique = true, length = 24)
    private String loanNumber;

    @Column(name = "product_type", nullable = false, length = 32)
    private String productType; // PERSONAL, HOME, AUTO, EDUCATION

    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    @Column(name = "emi_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal emiAmount;

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, DISBURSED, ACTIVE, CLOSED

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "closed_at")
    private Instant closedAt;
}
