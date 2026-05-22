package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.aspect.annotation.ValidateArguments;
import com.epam.laboratory.app.dto.ChangePasswordRequestDto;
import com.epam.laboratory.app.dto.CredentialsDto;
import com.epam.laboratory.app.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Authentication Management", description = "Endpoints for user authentication and token management")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    // ==================== GET MAPPINGS ====================

    @GetMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Authenticate user and return access and refresh tokens",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CredentialsDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authentication successful, tokens returned",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJKb2huLkRvZSIsImlhdCI6MTc3OTQ1Mzk3NywiZXhwIjoxNzc5NDU3NTc3fQ.DQFklRjyFCfi4L42QQ4ZLeQDEiPuzi9NAlcR9ded9DX18VmFx-XkkFgHEj6lDJyF",
                                                        "refreshToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJKb2huLkRvZSIsImlhdCI6MTc3OTQ1Mzk3NywiZXhwIjoxNzgwNzQ5OTc3fQ.duZfF30tnsvqbCxYo_weR_UrqDnDopdwJS-XAcey4sMmyLk_wdgMsudXlNZpqMvm"
                                                    }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid password or request format",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User with the given username does not exist",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    Map<String, String> login(@RequestBody CredentialsDto credentialsDto) {
        var username = credentialsDto.username();
        var password = credentialsDto.password();
        authenticationService.validateUser(username, password);
        return authenticationService.getUserTokens(username);
    }

    @GetMapping(
            path = "/refresh-token",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Refresh access token using a valid refresh token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(
                                    value = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJKb2huLkRvZSIsImlhdCI6MTc3OTQ0MzQ5MiwiZXhwIjoxNzgwNzM5NDkyfQ.tnYBM6AUOqk7QRvEArY1GBUhlGs68tA3dOMzQzQUH1hhgZPA_GEy-w-obtuWK2Wv"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access token refreshed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJKb2huLkRvZSIsImlhdCI6MTc3OTQ1MzY5NywiZXhwIjoxNzc5NDU3Mjk3fQ.m6AcgzHbGHxbTSbiD4mZQXWVHb4Ex2UmGmD72cH5iIh-XTV26_T-BPfHvUv6dPqI"
                                                     }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid or expired refresh token",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    Map<String, String> refreshAccessToken(@RequestBody String refreshToken) {
        return authenticationService.refreshAccessToken(refreshToken);
    }


    // ==================== PUT MAPPINGS ====================

    @PutMapping(
            path = "/{username}/change-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Change password for a user",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the user whose password is to be changed",
                    required = true,
                    example = "john.doe"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Old and new passwords",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChangePasswordRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password changed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request format or new password does not meet criteria",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Incorrect old password",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User with the given username does not exist",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    String changePassword(@PathVariable String username,
                          @RequestBody ChangePasswordRequestDto requestDto) {
        var oldPassword = requestDto.oldPassword();
        var newPassword = requestDto.newPassword();
        authenticationService.changePassword(username, oldPassword, newPassword);
        return "Password changed successfully";
    }

}
