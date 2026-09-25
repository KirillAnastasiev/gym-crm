package com.kirill.projects.gymcrm.app.dto;

import com.kirill.projects.gymcrm.app.dto.annotation.Required;
import com.kirill.projects.gymcrm.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"username", "password"})
@Schema(description = "User credentials for authentication")
public record CredentialsDto(
        @Required
        @JsonProperty("username")
        @Schema(
                description = "User's username",
                example = "John.Doe",
                requiredMode = RequiredMode.REQUIRED
        )
        String username,

        @Required
        @JsonProperty("password")
        @Sensitive
        @Schema(
                description = "User's password",
                example = "password123",
                requiredMode = RequiredMode.REQUIRED
        )
        String password
) {}
