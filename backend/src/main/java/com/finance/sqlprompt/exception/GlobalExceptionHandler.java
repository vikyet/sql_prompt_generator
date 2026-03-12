package com.finance.sqlprompt.exception;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Central handling for AI/agent failures so clients get consistent error shape and we can meter failures.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Counter agentQueryFailureCounter;

    public GlobalExceptionHandler(MeterRegistry registry) {
        this.agentQueryFailureCounter = registry.counter("finance.agent.query.failures");
    }

    @ExceptionHandler(AgentQueryException.class)
    public ProblemDetail handleAgentQueryException(AgentQueryException ex) {
        agentQueryFailureCounter.increment();
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Agent Query Error");
        return detail;
    }
}
