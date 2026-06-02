package com.epam.laboratory.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"message"})
@Schema(description = "Response containing a message, typically used for informational responses")
public record MessageResponseDto(
        @JsonProperty("message")
        @Schema(
                description = "Informational message describing the result of an operation",
                example = "Password changed successfully"
        )
        String message
) {}
