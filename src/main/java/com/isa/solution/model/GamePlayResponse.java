package com.isa.solution.model;

import java.util.List;

public record GamePlayResponse(
    int totalGamesPlayed,
    int successfulGames,
    int failedGames,
    double averageScore,
    int highestScore,
    int lowestScore,
    List<GameResult> gameResults
) {
}
