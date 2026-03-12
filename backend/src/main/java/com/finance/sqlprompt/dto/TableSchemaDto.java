package com.finance.sqlprompt.dto;

import java.util.List;

/**
 * Exposes table metadata to the AI agent so it can build valid SQL without guessing column names.
 */
public record TableSchemaDto(String tableName, String description, List<ColumnSchemaDto> columns) {

    public record ColumnSchemaDto(String name, String type, String description) {}
}
