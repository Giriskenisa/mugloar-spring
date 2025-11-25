package com.isa.solution.exception;

public class DragonsApiException extends RuntimeException {

    private final String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public DragonsApiException(String message) {
        super(message);
        this.errorCode = "DRAGONS_API_ERROR";
    }

    public DragonsApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DRAGONS_API_ERROR";
    }

    public DragonsApiException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public DragonsApiException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}