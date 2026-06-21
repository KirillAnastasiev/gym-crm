package com.epam.laboratory.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"oldPassword", "newPassword"})
public record ChangePasswordRequest(
        @JsonProperty(value = "oldPassword", required = true)
        String oldPassword,

        @JsonProperty(value = "newPassword", required = true)
        String newPassword
) {}
