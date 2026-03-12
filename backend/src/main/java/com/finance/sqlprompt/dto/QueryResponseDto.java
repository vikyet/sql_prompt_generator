package com.finance.sqlprompt.dto;

import java.util.List;
import java.util.Map;

/**
 * Structured result for the agent—list of rows as key-value maps for JSON serialization.
 */
public record QueryResponseDto(
    List<String> columns,
    List<Map<String, Object>> rows,
    int rowCount
) {}
