package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.aspect.annotation.ValidateArguments;
import com.epam.laboratory.app.dto.*;
import com.epam.laboratory.app.dto.mapper.TokenResponseDtoMapper;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Authentication Management", description = "Endpoints for user authentication and token management")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TokenResponseDtoMapper tokenResponseDtoMapper;


    // ==================== POST MAPPINGS ====================

    @PostMapping(
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
                                    schema = @Schema(implementation = TokensResponseDto.class)
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
    TokensResponseDto login(@RequestBody CredentialsDto credentialsDto) {
        var username = credentialsDto.username();
        var password = credentialsDto.password();
        var userTokens = authenticationService.getUserTokens(username, password);
        return tokenResponseDtoMapper.toDto(userTokens);
    }

    @PostMapping(
            path = "/{username}/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Logout user by invalidating the provided refresh token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout successful, access and refresh tokens invalidated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageResponseDto.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "message": "Logout successful"
                                            }
                                            """)
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
    MessageResponseDto logout(@PathVariable String username) {
        authenticationService.logout(username);
        return new MessageResponseDto("Logout successful, access and refresh tokens invalidated");
    }

    @PostMapping(
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
                            schema = @Schema(implementation = RefreshTokenRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access token refreshed successfully, new tokens returned",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TokensResponseDto.class)
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
    TokensResponseDto refreshAccessToken(@RequestBody RefreshTokenRequestDto refreshTokenDto) {
        var newTokens = authenticationService.refreshAccessToken(refreshTokenDto.refreshToken());
        return tokenResponseDtoMapper.toDto(newTokens);
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
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageResponseDto.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "message": "Password changed successfully"
                                            }
                                            """)
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
    MessageResponseDto changePassword(@PathVariable String username,
                                      @RequestBody ChangePasswordRequestDto requestDto) {
        var oldPassword = requestDto.oldPassword();
        var newPassword = requestDto.newPassword();
        authenticationService.changePassword(username, oldPassword, newPassword);
        return new MessageResponseDto("Password changed successfully");
    }

}
