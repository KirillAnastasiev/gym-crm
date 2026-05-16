package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.aspect.annotation.ValidateArguments;
import com.epam.laboratory.app.dto.ChangePasswordRequestDto;
import com.epam.laboratory.app.dto.CredentialsDto;
import com.epam.laboratory.app.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping(
            path = "/login",
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    ResponseEntity<Map<String, String>> login(@RequestBody CredentialsDto credentialsDto) {
        var username = credentialsDto.username();
        var password = credentialsDto.password();
        authenticationService.validateUser(username, password);
        var tokens = authenticationService.getUserTokens(username);
        return ResponseEntity.ok(tokens);
    }

    @GetMapping(
            path = "/refresh-token",
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody String refreshToken) {
        var newAccessToken = authenticationService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    @PutMapping(
            path = "/{username}/change-password",
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    ResponseEntity<String> changePassword(@PathVariable String username,
                                          @RequestBody ChangePasswordRequestDto requestDto) {
        var oldPassword = requestDto.oldPassword();
        var newPassword = requestDto.newPassword();
        authenticationService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }

}
