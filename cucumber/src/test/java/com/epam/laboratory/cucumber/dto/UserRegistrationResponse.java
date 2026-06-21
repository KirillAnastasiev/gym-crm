package com.epam.laboratory.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"username", "password"})
public record UserRegistrationResponse(
        @JsonProperty(value = "username", required = true)
        String username,

        @JsonProperty(value = "password", required = true)
        String password
) {}
