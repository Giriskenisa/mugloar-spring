package com.isa.solution.exception;

/**
 * Exception thrown when the request parameters are invalid.
 */
public class InvalidRequestException extends DragonsApiException {

    private final String parameterName;
    private final Object invalidValue;

    public String getParameterName() {
        return parameterName;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public InvalidRequestException(String message) {
        super("INVALID_REQUEST", message);
        this.parameterName = null;
        this.invalidValue = null;
    }

    public InvalidRequestException(String parameterName, Object invalidValue, String message) {
        super("INVALID_REQUEST",
                String.format("Invalid parameter '%s' with value '%s': %s",
                        parameterName, invalidValue, message));
        this.parameterName = parameterName;
        this.invalidValue = invalidValue;
    }

    public InvalidRequestException(String message, Throwable cause) {
        super("INVALID_REQUEST", message, cause);
        this.parameterName = null;
        this.invalidValue = null;
    }
}
