package com.isa.solution.model;

public record GameResult(
    String gameId,
    int finalScore,
    int turnsPlayed,
    boolean success,
    String failureReason
) {
}
