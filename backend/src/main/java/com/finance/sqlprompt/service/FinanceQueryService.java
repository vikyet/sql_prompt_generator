package com.finance.sqlprompt.service;

import com.finance.sqlprompt.dto.QueryRequestDto;
import com.finance.sqlprompt.dto.QueryResponseDto;
import com.finance.sqlprompt.exception.AgentQueryException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Executes read-only SELECTs from the agent. Enforces SELECT-only and timeout to prevent abuse and long-running queries.
 */
@Service
public class FinanceQueryService {

    private static final Pattern SELECT_ONLY = Pattern.compile(
        "^\\s*SELECT\\s+.+",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Set<String> FORBIDDEN_KEYWORDS = Set.of(
        "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER", "TRUNCATE",
        "EXEC", "EXECUTE", "CALL", ";\\s*--", "/*", "*/"
    );

    private final DataSource dataSource;
    private final int queryTimeoutSeconds;
    private final Timer queryTimer;

    public FinanceQueryService(
            DataSource dataSource,
            @Value("${app.query.timeout-seconds:30}") int queryTimeoutSeconds,
            MeterRegistry registry) {
        this.dataSource = dataSource;
        this.queryTimeoutSeconds = queryTimeoutSeconds;
        this.queryTimer = registry.timer("finance.query.execute");
    }

    public QueryResponseDto execute(QueryRequestDto request) {
        String sql = request.sql().trim();
        validateSql(sql);

        return queryTimer.record(() -> {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(queryTimeoutSeconds);
                boolean isResultSet = stmt.execute(sql);
                if (!isResultSet) {
                    throw new AgentQueryException("Only SELECT statements are allowed");
                }
                try (ResultSet rs = stmt.getResultSet()) {
                    return mapToResponse(rs);
                }
            } catch (SQLException e) {
                throw new AgentQueryException("Query failed: " + e.getMessage(), e);
            }
        });
    }

    private void validateSql(String sql) {
        if (!SELECT_ONLY.matcher(sql).matches()) {
            throw new AgentQueryException("Only SELECT statements are allowed");
        }
        String upper = sql.toUpperCase();
        for (String kw : FORBIDDEN_KEYWORDS) {
            if (upper.contains(kw)) {
                throw new AgentQueryException("Statement contains forbidden keyword or pattern: " + kw);
            }
        }
    }

    private QueryResponseDto mapToResponse(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        List<String> columns = new ArrayList<>(colCount);
        for (int i = 1; i <= colCount; i++) {
            columns.add(meta.getColumnLabel(i));
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= colCount; i++) {
                Object value = rs.getObject(i);
                if (value instanceof Timestamp ts) {
                    value = ts.toInstant().toString();
                }
                row.put(columns.get(i - 1), value);
            }
            rows.add(row);
        }
        return new QueryResponseDto(columns, rows, rows.size());
    }
}
