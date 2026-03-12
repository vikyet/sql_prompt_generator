package com.finance.sqlprompt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Read-only query from the agent. Backend enforces SELECT-only and timeout.
 */
public record QueryRequestDto(
    @NotBlank
    @Size(max = 4096)
    String sql
) {}
