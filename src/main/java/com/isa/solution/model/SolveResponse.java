package com.isa.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SolveResponse(
    @JsonProperty("success") boolean success,
    @JsonProperty("lives") Integer lives,
    @JsonProperty("gold") Integer gold,
    @JsonProperty("score") Integer score,
    @JsonProperty("highScore") Integer highScore,
    @JsonProperty("turn") Integer turn,
    @JsonProperty("message") String message
) {
}
