package com.finance.sqlprompt.repository;

import com.finance.sqlprompt.entity.Branch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JPA repository test: Branch and derived queries.
 */
@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class BranchRepositoryTest {

    @Autowired
    BranchRepository branchRepository;

    @Test
    void findByCode() {
        Optional<Branch> branch = branchRepository.findByCode("BR-T1");
        assertThat(branch).isPresent();
        assertThat(branch.get().getName()).isEqualTo("Test Branch");
        assertThat(branch.get().getCity()).isEqualTo("Hyderabad");
    }

    @Test
    void findByStatus() {
        List<Branch> active = branchRepository.findByStatus("ACTIVE");
        assertThat(active).isNotEmpty();
    }
}
