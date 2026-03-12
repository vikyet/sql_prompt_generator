package com.finance.sqlprompt;

import com.finance.sqlprompt.dto.QueryRequestDto;
import com.finance.sqlprompt.dto.QueryResponseDto;
import com.finance.sqlprompt.exception.AgentQueryException;
import com.finance.sqlprompt.service.FinanceQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies read-only query execution and validation. Uses in-memory H2 for speed.
 */
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class FinanceQueryServiceTest {

    @Autowired
    FinanceQueryService queryService;

    @Test
    void executeSelectReturnsRows() {
        QueryRequestDto request = new QueryRequestDto("SELECT id, customer_ref FROM customer LIMIT 2");
        QueryResponseDto response = queryService.execute(request);
        assertThat(response.columns()).containsExactlyInAnyOrder("id", "customer_ref");
        assertThat(response.rowCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void rejectNonSelect() {
        assertThatThrownBy(() -> queryService.execute(new QueryRequestDto("INSERT INTO customer (customer_ref, status) VALUES ('X','ACTIVE')")))
            .isInstanceOf(AgentQueryException.class)
            .hasMessageContaining("Only SELECT");
    }

    @Test
    void rejectDelete() {
        assertThatThrownBy(() -> queryService.execute(new QueryRequestDto("DELETE FROM customer")))
            .isInstanceOf(AgentQueryException.class);
    }
}
