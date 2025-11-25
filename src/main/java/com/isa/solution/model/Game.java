package com.isa.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Game(
    @JsonProperty("gameId") String gameId,
    @JsonProperty("lives") int lives,
    @JsonProperty("gold") int gold,
    @JsonProperty("level") int level,
    @JsonProperty("score") int score,
    @JsonProperty("turn") int turn,
    @JsonProperty("highScore") int highScore
) {
}
