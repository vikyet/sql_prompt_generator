package com.finance.sqlprompt.repository;

import com.finance.sqlprompt.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findByLoanNumber(String loanNumber);

    List<Loan> findByCustomer_Id(Long customerId);

    List<Loan> findByBranch_Id(Long branchId);

    List<Loan> findByStatus(String status);
}
