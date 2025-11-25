package com.isa.solution.exception;

public class GameNotFoundException extends DragonsApiException {

    private final String gameId;

    public String getGameId() {
        return gameId;
    }

    public GameNotFoundException(String gameId) {
        super("GAME_NOT_FOUND", String.format("Game with ID '%s' was not found", gameId));
        this.gameId = gameId;
    }

    public GameNotFoundException(String gameId, Throwable cause) {
        super("GAME_NOT_FOUND", String.format("Game with ID '%s' was not found", gameId), cause);
        this.gameId = gameId;
    }

}

