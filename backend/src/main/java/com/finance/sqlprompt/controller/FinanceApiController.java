package com.finance.sqlprompt.controller;

import com.finance.sqlprompt.dto.QueryRequestDto;
import com.finance.sqlprompt.dto.QueryResponseDto;
import com.finance.sqlprompt.dto.TableSchemaDto;
import com.finance.sqlprompt.service.FinanceQueryService;
import com.finance.sqlprompt.service.FinanceSchemaService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for the Python agent: schema discovery and read-only query execution.
 */
@RestController
@RequestMapping("/api")
public class FinanceApiController {

    private final FinanceSchemaService schemaService;
    private final FinanceQueryService queryService;

    public FinanceApiController(FinanceSchemaService schemaService, FinanceQueryService queryService) {
        this.schemaService = schemaService;
        this.queryService = queryService;
    }

    @GetMapping(value = "/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TableSchemaDto> getSchema() {
        return schemaService.getSchema();
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public QueryResponseDto executeQuery(@Valid @RequestBody QueryRequestDto request) {
        return queryService.execute(request);
    }
}
