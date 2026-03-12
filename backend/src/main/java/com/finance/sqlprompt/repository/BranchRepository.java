package com.finance.sqlprompt.repository;

import com.finance.sqlprompt.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByCode(String code);

    List<Branch> findByStatus(String status);

    List<Branch> findByCity(String city);
}
