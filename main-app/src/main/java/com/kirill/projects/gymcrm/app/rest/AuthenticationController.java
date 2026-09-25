package com.kirill.projects.gymcrm.app.rest;

import com.kirill.projects.gymcrm.app.aspect.annotation.RestCallLogging;
import com.kirill.projects.gymcrm.app.aspect.annotation.ValidateArguments;
import com.kirill.projects.gymcrm.app.dto.ChangePasswordRequestDto;
import com.kirill.projects.gymcrm.app.dto.MessageResponseDto;
import com.kirill.projects.gymcrm.app.dto.RefreshTokenRequestDto;
import com.kirill.projects.gymcrm.app.dto.TokensResponseDto;
import com.kirill.projects.gymcrm.app.dto.mapper.TokenResponseDtoMapper;
import com.kirill.projects.gymcrm.app.service.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Authentication Management", description = "Endpoints for user authentication and token management")
public class AuthenticationController implements Controller {

    private final AuthenticationService authenticationService;
    private final TokenResponseDtoMapper tokenResponseDtoMapper;


    // ==================== GET MAPPINGS ====================

    @GetMapping(
            path = "/tokens",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "basicAuth")
    @Operation(
            description = "Authenticate user and return access and refresh tokens",
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
                            description = "Invalid username, password or request format",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    ResponseEntity<TokensResponseDto> getTokens(@RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            var username = authentication.getName();
            var userTokens = authenticationService.getUserTokens(username);
            return tokenResponseDtoMapper.toDto(userTokens);
        }, HttpStatus.OK);
    }


    // ==================== POST MAPPINGS ====================

    @PostMapping(
            path = "/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "bearerAuth")
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
    ResponseEntity<MessageResponseDto> logout(@RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            var username = authentication.getName();
            authenticationService.logout(username);
            return new MessageResponseDto("Logout successful, access and refresh tokens invalidated");
        }, HttpStatus.OK);
    }

    @PostMapping(
            path = "/refresh-token",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
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
                            description = "Token refreshed successfully, new tokens returned",
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
    ResponseEntity<TokensResponseDto> refreshAccessToken(@RequestBody RefreshTokenRequestDto refreshTokenDto,
                                                         @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            var newTokens = authenticationService.refreshAccessToken(refreshTokenDto.refreshToken());
            return tokenResponseDtoMapper.toDto(newTokens);
        }, HttpStatus.OK);
    }


    // ==================== PUT MAPPINGS ====================

    @PutMapping(
            path = "/change-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "bearerAuth")
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
                            responseCode = "401",
                            description = "Incorrect authentication credentials or invalid request format",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
            }
    )
    ResponseEntity<MessageResponseDto> changePassword(@RequestBody ChangePasswordRequestDto requestDto,
                                                      @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            var username = authentication.getName();
            var oldPassword = requestDto.oldPassword();
            var newPassword = requestDto.newPassword();
            authenticationService.changePassword(username, oldPassword, newPassword);
            return new MessageResponseDto("Password changed successfully");
        }, HttpStatus.OK);
    }

}
