package com.isa.solution.exception;

public class ApiResponseException extends DragonsApiException {

    private final int statusCode;
    private final String responseBody;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public ApiResponseException(int statusCode, String message, String responseBody) {
        super("API_RESPONSE_ERROR",
                String.format("API returned error status %d: %s", statusCode, message));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ApiResponseException(int statusCode, String message, String responseBody, Throwable cause) {
        super("API_RESPONSE_ERROR",
                String.format("API returned error status %d: %s", statusCode, message),
                cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}