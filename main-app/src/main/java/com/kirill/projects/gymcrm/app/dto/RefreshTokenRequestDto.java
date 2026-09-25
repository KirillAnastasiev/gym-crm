package com.kirill.projects.gymcrm.app.dto;

import com.kirill.projects.gymcrm.app.dto.annotation.Required;
import com.kirill.projects.gymcrm.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"refreshToken"})
public record RefreshTokenRequestDto(
        @Required
        @JsonProperty("refreshToken")
        @Sensitive
        @Schema(
                description = "Refresh token used to obtain a new access token when the current one expires",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKb2huRG9lIiwiaWF0IjoxNjg4ODg4ODg4LCJleHAiOjE2ODg5MjQ4ODh9.def456ghi789jkl012mno345pqr678stu901vwx234yz567890",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String refreshToken
) {}
