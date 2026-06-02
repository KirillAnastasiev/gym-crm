package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"accessToken", "refreshToken"})
@Schema(description = "Response containing access and refresh tokens after successful authentication")
public record TokensResponseDto(
        @JsonProperty("accessToken")
        @Sensitive
        @Schema(
                description = "JWT access token used for authenticating API requests",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKb2huRG9lIiwiaWF0IjoxNjg4ODg4ODg4LCJleHAiOjE2ODg4OTI0ODh9.abc123def456ghi789jkl012mno345pqr678stu901vwx234yz567890"
        )
        String accessToken,

        @JsonProperty("refreshToken")
        @Sensitive
        @Schema(
                description = "Refresh token used to obtain new access tokens when the current one expires",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKb2huRG9lIiwiaWF0IjoxNjg4ODg4ODg4LCJleHAiOjE2ODg5MjQ4ODh9.def456ghi789jkl012mno345pqr678stu901vwx234yz567890"
        )
        String refreshToken
) {}
