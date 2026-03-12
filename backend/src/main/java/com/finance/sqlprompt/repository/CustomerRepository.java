package com.finance.sqlprompt.repository;

import com.finance.sqlprompt.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
