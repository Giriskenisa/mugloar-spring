package com.isa.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PurchaseResponse(
    @JsonProperty("shoppingSuccess") boolean shoppingSuccess,
    @JsonProperty("lives") Integer lives,
    @JsonProperty("gold") Integer gold,
    @JsonProperty("level") Integer level,
    @JsonProperty("turn") Integer turn
) {
}
