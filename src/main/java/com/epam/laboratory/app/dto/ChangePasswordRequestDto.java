package com.epam.laboratory.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"username", "oldPassword", "newPassword"})
public record ChangePasswordRequestDto(
        @JsonProperty(value = "username", required = true) String username,
        @JsonProperty(value = "oldPassword", required = true) String oldPassword,
        @JsonProperty(value = "newPassword", required = true) String newPassword
) {}