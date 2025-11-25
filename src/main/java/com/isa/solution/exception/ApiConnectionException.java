package com.isa.solution.exception;

public class ApiConnectionException extends DragonsApiException {

    private final String endpoint;

    public String getEndpoint() {
        return endpoint;
    }

    public ApiConnectionException(String endpoint, String message) {
        super("API_CONNECTION_ERROR",
                String.format("Failed to connect to endpoint '%s': %s", endpoint, message));
        this.endpoint = endpoint;
    }

    public ApiConnectionException(String endpoint, String message, Throwable cause) {
        super("API_CONNECTION_ERROR",
                String.format("Failed to connect to endpoint '%s': %s", endpoint, message),
                cause);
        this.endpoint = endpoint;
    }

    public ApiConnectionException(String message, Throwable cause) {
        super("API_CONNECTION_ERROR", message, cause);
        this.endpoint = null;
    }

}
