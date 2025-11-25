package com.isa.solution.exception;

public class InvalidGameStateException extends DragonsApiException {

    private final String gameId;
    private final String currentState;

    public String getGameId() {
        return gameId;
    }

    public String getCurrentState() {
        return currentState;
    }

    public InvalidGameStateException(String gameId, String currentState, String message) {
        super("INVALID_GAME_STATE", message);
        this.gameId = gameId;
        this.currentState = currentState;
    }

    public InvalidGameStateException(String message) {
        super("INVALID_GAME_STATE", message);
        this.gameId = null;
        this.currentState = null;
    }
}
