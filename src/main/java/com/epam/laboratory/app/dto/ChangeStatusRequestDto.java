package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Required;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"active"})
@Schema(description = "Request for changing user status")
public record ChangeStatusRequestDto(
        @Required
        @JsonProperty("active")
        @Schema(
                description = "New user's status",
                example = "true",
                requiredMode = RequiredMode.REQUIRED
        )
        boolean active
) {}
