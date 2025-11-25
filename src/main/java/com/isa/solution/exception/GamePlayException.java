package com.isa.solution.exception;

public class GamePlayException extends DragonsApiException {

    public GamePlayException(String message) {
        super(message);
    }

    public GamePlayException(String message, Throwable cause) {
        super(message, cause);
    }
}
