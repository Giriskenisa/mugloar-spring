package com.isa.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
    @JsonProperty("adId") String adId,
    @JsonProperty("message") String message,
    @JsonProperty("probability") String probability,
    @JsonProperty("expiresIn") int expiresIn,
    @JsonProperty("reward") int reward,
    @JsonProperty("encrypted") Integer encrypted
) {
}
