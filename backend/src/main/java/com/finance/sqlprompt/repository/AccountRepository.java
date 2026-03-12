package com.finance.sqlprompt.repository;

import com.finance.sqlprompt.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCustomer_Id(Long customerId);

    List<Account> findByStatus(String status);
}
