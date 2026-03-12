package com.finance.sqlprompt.exception;

/**
 * Raised when agent-originated query is invalid, not allowed, or times out. Enables custom handling and metrics.
 */
public class AgentQueryException extends RuntimeException {

    public AgentQueryException(String message) {
        super(message);
    }

    public AgentQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
