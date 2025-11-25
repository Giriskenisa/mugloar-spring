package com.isa.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShopItem(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("cost") int cost
) {
}
