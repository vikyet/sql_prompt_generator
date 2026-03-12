package com.finance.sqlprompt.repository;

import com.finance.sqlprompt.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccount_IdOrderByCreatedAtDesc(Long accountId, org.springframework.data.domain.Pageable pageable);

    List<Transaction> findByAccount_IdAndCreatedAtBetween(Long accountId, Instant from, Instant to);
}
