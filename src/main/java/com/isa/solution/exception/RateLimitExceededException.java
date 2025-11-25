package com.isa.solution.exception;

/**
 * Exception thrown when API rate limit is exceeded.
 */
public class RateLimitExceededException extends DragonsApiException {

    private final Integer retryAfterSeconds;

    public Integer getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    public RateLimitExceededException() {
        super("RATE_LIMIT_EXCEEDED", "API rate limit has been exceeded");
        this.retryAfterSeconds = null;
    }

    public RateLimitExceededException(Integer retryAfterSeconds) {
        super("RATE_LIMIT_EXCEEDED",
                String.format("API rate limit exceeded. Retry after %d seconds", retryAfterSeconds));
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public RateLimitExceededException(String message) {
        super("RATE_LIMIT_EXCEEDED", message);
        this.retryAfterSeconds = null;
    }

}
