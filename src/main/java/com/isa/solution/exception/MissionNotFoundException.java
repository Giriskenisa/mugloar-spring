package com.isa.solution.exception;

public class MissionNotFoundException extends DragonsApiException {

    private final String missionId;
    private final String gameId;

    public String getMissionId() {
        return missionId;
    }

    public String getGameId() {
        return gameId;
    }

    public MissionNotFoundException(String gameId, String missionId) {
        super("MISSION_NOT_FOUND",
                String.format("Mission with ID '%s' was not found in game '%s'", missionId, gameId));
        this.missionId = missionId;
        this.gameId = gameId;
    }

    public MissionNotFoundException(String gameId, String missionId, Throwable cause) {
        super("MISSION_NOT_FOUND",
                String.format("Mission with ID '%s' was not found in game '%s'", missionId, gameId),
                cause);
        this.missionId = missionId;
        this.gameId = gameId;
    }
}