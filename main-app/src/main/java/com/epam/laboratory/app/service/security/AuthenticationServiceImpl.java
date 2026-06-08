package com.epam.laboratory.app.service.security;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.AuthenticationDao;
import com.epam.laboratory.app.util.InputDataValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.epam.laboratory.app.security.JwtToken.JwtTokenType;
import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationDao authenticationDao;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public boolean checkExistsByUsername(String username) {
        InputDataValidator.validateNotBlank(username, "Username");
        return authenticationDao.checkExistsByUsername(username);
    }

    @Logging(INFO)
    @Override
    public Map<String, String> getUserTokens(String username) {
        InputDataValidator.validateNotBlank(username, "Username");
        jwtService.revokeTokens(username);
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
        jwtService.validateToken(tokenObject, JwtTokenType.REFRESH);
        String username = jwtService.getUsernameFromToken(tokenObject);
        boolean exists = checkExistsByUsername(username);
        if (!exists) {
            throw new IllegalArgumentException("User with username %s does not exist".formatted(username));
        }
        jwtService.revokeTokens(username);
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
        jwtService.revokeTokens(username);
    }

    @Logging(INFO)
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        InputDataValidator.validateNotBlank(newPassword, "New password");
        validateUser(username, oldPassword);
        var encodedNewPassword = passwordEncoder.encode(newPassword);
        authenticationDao.changePasswordForUsername(username, encodedNewPassword);
        jwtService.revokeTokens(username);
    }

    private void validateUser(String username, String password) {
        InputDataValidator.validateNotBlank(username, "Username");
        InputDataValidator.validateNotBlank(password, "Password");
        var exists = checkExistsByUsername(username);
        if (!exists) {
            throw new NoSuchEntityException("User with username %s does not exist".formatted(username));
        }
        checkPasswordForUsername(username, password);
    }

    private void checkPasswordForUsername(String username, String password) {
        authenticationDao.findUserByUsername(username, false)
                .ifPresentOrElse(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        throw new AuthenticationException("Incorrect password for username %s".formatted(username));
                    }
                }, () -> {
                    throw new NoSuchEntityException("User with username %s does not exist".formatted(username));
                });
    }

}
