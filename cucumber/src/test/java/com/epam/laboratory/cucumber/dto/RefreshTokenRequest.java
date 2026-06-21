package com.epam.laboratory.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"refreshToken"})
public record RefreshTokenRequest(
        @JsonProperty(value = "refreshToken", required = true)
        String refreshToken
) {}
