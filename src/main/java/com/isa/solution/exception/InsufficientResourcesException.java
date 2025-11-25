package com.isa.solution.exception;


public class InsufficientResourcesException extends DragonsApiException {

    public InsufficientResourcesException(String message) {
        super("INSUFFICIENT_RESOURCES", message);
        String resourceType = null;
        Integer required = null;
        Integer available = null;
    }
}
