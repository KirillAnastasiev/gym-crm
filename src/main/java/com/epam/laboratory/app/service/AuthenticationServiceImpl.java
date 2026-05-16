package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.repository.AuthenticationDao;
import com.epam.laboratory.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationDao authenticationDao;
    private final JwtUtil jwtUtil;

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public boolean checkExistsByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        return authenticationDao.checkExistsByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkPasswordForUsername(String username, String password) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        return authenticationDao.checkPasswordForUsername(username, password);
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public void validateUser(String username, String password) {
        var exists = checkExistsByUsername(username);
        if (!exists) {
            throw new AuthenticationException("User with username %s does not exist".formatted(username));
        }

        var passwordIsCorrect = checkPasswordForUsername(username, password);
        if (!passwordIsCorrect) {
            throw new AuthenticationException("Incorrect password for username %s".formatted(username));
        }
    }

    @Logging(INFO)
    @Override
    public Map<String, String> getUserTokens(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }

        Map<String, String> userTokens = new HashMap<>();
        var accessToken = jwtUtil.generateAccessToken(username);
        var refreshToken = jwtUtil.generateRefreshToken(username);
        userTokens.put("accessToken", accessToken);
        userTokens.put("refreshToken", refreshToken);
        return userTokens;
    }

    @Logging(INFO)
    @Override
    public Map<String, String> refreshAccessToken(String refreshToken) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token must not be null");
        }
        if (refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token must not be blank");
        }
        var isValid = jwtUtil.validateToken(refreshToken);
        if (!isValid) {
            throw new AuthenticationException("Invalid refresh token");
        }

        var optionalUsername = jwtUtil.getUsernameFromToken(refreshToken);
        var username = optionalUsername.orElseThrow(() ->
                new AuthenticationException("Username not found in refresh token"));

        var newAccessToken = jwtUtil.generateAccessToken(username);
        Map<String, String> newToken = new HashMap<>();
        newToken.put("accessToken", newAccessToken);
        return newToken;
    }

    @Logging(INFO)
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if  (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (oldPassword == null) {
            throw new IllegalArgumentException("Old password must not be null");
        }
        if (oldPassword.isBlank()) {
            throw new IllegalArgumentException("Old password must not be blank");
        }
        if (newPassword == null) {
            throw new IllegalArgumentException("New password must not be null");
        }
        if (newPassword.isBlank()) {
            throw new IllegalArgumentException("New password must not be blank");
        }
        validateUser(username, oldPassword);
        authenticationDao.changePasswordForUsername(username, newPassword);
    }

}
