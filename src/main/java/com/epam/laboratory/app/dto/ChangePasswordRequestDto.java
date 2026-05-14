package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Required;
import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"oldPassword", "newPassword"})
public record ChangePasswordRequestDto(
        @Required @JsonProperty("oldPassword") @Sensitive String oldPassword,
        @Required @JsonProperty("newPassword") @Sensitive String newPassword
) {}