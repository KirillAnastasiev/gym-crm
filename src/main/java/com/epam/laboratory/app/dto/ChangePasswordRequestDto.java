package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Required;
import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"oldPassword", "newPassword"})
@Schema(description = "Request for changing user's password")
public record ChangePasswordRequestDto(
        @Required
        @JsonProperty("oldPassword")
        @Sensitive
        @Schema(
                description = "Old password value",
                example = "oldPassword123",
                requiredMode = RequiredMode.REQUIRED
        )
        String oldPassword,

        @Required
        @JsonProperty("newPassword")
        @Sensitive
        @Schema(
                description = "New password value",
                example = "newPassword456",
                requiredMode = RequiredMode.REQUIRED
        )
        String newPassword
) {}