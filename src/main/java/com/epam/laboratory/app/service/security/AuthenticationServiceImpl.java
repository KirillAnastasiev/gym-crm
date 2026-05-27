package com.epam.laboratory.app.service.security;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.AuthenticationDao;
import com.epam.laboratory.app.util.InputDataValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.epam.laboratory.app.security.JwtToken.*;
import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationDao authenticationDao;
    private final JwtService jwtService;

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public boolean checkExistsByUsername(String username) {
        InputDataValidator.validateNotBlank(username, "Username");
        return authenticationDao.checkExistsByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkPasswordForUsername(String username, String password) {
        InputDataValidator.validateNotBlank(username, "Username");
        InputDataValidator.validateNotBlank(password, "Password");
        return authenticationDao.checkPasswordForUsername(username, password);
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public void validateUser(String username, String password) {
        InputDataValidator.validateNotBlank(username, "Username");
        InputDataValidator.validateNotBlank(password, "Password");
        var exists = checkExistsByUsername(username);
        if (!exists) {
            throw new NoSuchEntityException("User with username %s does not exist".formatted(username));
        }
        var passwordIsCorrect = checkPasswordForUsername(username, password);
        if (!passwordIsCorrect) {
            throw new AuthenticationException("Incorrect password for username %s".formatted(username));
        }
    }

    @Logging(INFO)
    @Override
    public Map<String, String> getUserTokens(String username, String password) {
        validateUser(username, password);
        jwtService.revokeToken(username, JwtTokenType.ACCESS);
        jwtService.revokeToken(username, JwtTokenType.REFRESH);
        var accessToken = jwtService.createAccessToken(username);
        var refreshToken = jwtService.createRefreshToken(username);
        return Map.of(
                "accessToken", jwtService.serializeToken(accessToken),
                "refreshToken", jwtService.serializeToken(refreshToken)
        );
    }

    @Logging(INFO)
    @Override
    public Map<String, String> refreshAccessToken(String refreshToken) {
        InputDataValidator.validateNotBlank(refreshToken, "Refresh token");
        var tokenObject = jwtService.deserializeToken(refreshToken);
        jwtService.validateToken(tokenObject);
        jwtService.revokeToken(tokenObject);
        String username = jwtService.getUsernameFromToken(tokenObject);
        boolean exists = checkExistsByUsername(username);
        if (!exists) {
            throw new IllegalArgumentException("User with username %s does not exist".formatted(username));
        }
        var newAccessToken = jwtService.createAccessToken(username);
        var newRefreshToken = jwtService.createRefreshToken(username);
        return Map.of(
                "accessToken", jwtService.serializeToken(newAccessToken),
                "refreshToken", jwtService.serializeToken(newRefreshToken)
        );
    }

    @Logging(INFO)
    @Override
    public void logout(String username) {
        InputDataValidator.validateNotBlank(username, "Username");
        jwtService.revokeToken(username, JwtTokenType.ACCESS);
        jwtService.revokeToken(username, JwtTokenType.REFRESH);
    }

    @Logging(INFO)
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        InputDataValidator.validateNotBlank(newPassword, "New password");
        validateUser(username, oldPassword);
        authenticationDao.changePasswordForUsername(username, newPassword);
        jwtService.revokeToken(username, JwtTokenType.ACCESS);
        jwtService.revokeToken(username, JwtTokenType.REFRESH);
    }

}
