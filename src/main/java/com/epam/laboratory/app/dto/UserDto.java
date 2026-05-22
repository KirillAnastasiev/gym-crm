package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Required;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"username"})
@Schema(description = "Username information for authentication")
public record UserDto(
        @Required
        @JsonProperty(value = "username", required = true)
        @Schema(
                description = "User's username",
                example = "John.Doe",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String username
) {}
