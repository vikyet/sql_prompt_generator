package com.finance.sqlprompt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Retail branch: physical location. Referenced by customers, accounts, transactions, loans.
 * JPA: root entity with no FK; others reference Branch via @ManyToOne.
 */
@Entity
@Table(name = "branch", indexes = {
    @Index(name = "idx_branch_code", columnList = "code"),
    @Index(name = "idx_branch_status", columnList = "status"),
    @Index(name = "idx_branch_city", columnList = "city")
})
@Getter
@Setter
@NoArgsConstructor
public class Branch extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(length = 64)
    private String city;

    @Column(length = 64)
    private String state;

    @Column(length = 12)
    private String pincode;

    @Column(length = 24)
    private String phone;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "branch", cascade = {}, fetch = FetchType.LAZY)
    private List<Customer> customers = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = {}, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = {}, fetch = FetchType.LAZY)
    private List<Loan> loans = new ArrayList<>();
}
