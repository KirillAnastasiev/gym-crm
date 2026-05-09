package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.dto.ChangePasswordRequestDto;
import com.epam.laboratory.app.dto.CredentialsDto;
import com.epam.laboratory.app.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping(path = "/login", consumes = "application/json", produces = "application/json")
    ResponseEntity<String> login(@RequestBody CredentialsDto credentialsDto) {
        var username = credentialsDto.username();
        var password = credentialsDto.password();
        authenticationService.validateUser(username, password);
        return ResponseEntity.ok("Logged in successfully");
    }

    @PutMapping(path = "/change-password", consumes = "application/json", produces = "application/json")
    ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto requestDto) {
        var username = requestDto.username();
        var oldPassword = requestDto.oldPassword();
        var newPassword = requestDto.newPassword();
        authenticationService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }

}
