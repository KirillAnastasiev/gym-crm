package com.epam.laboratory.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"username", "password"})
public record CredentialsDto(
        @JsonProperty(value = "username", required = true) String username,
        @JsonProperty(value = "password", required = true) String password
) {}
