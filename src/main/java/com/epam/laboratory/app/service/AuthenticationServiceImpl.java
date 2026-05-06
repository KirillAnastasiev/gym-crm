package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.repository.AuthenticationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationDao authenticationDao;

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

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public boolean checkPasswordForUsername(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password must not be null");
        }
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Username and password must not be blank");
        }
        return authenticationDao.checkPasswordForUsername(username, password);
    }

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
    public void changePassword(String username, String oldPassword, String newPassword) {
        if  (username == null || oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException("Username, old password and new password must not be null");
        }
        if (username.isBlank() || oldPassword.isBlank() || newPassword.isBlank()) {
            throw new IllegalArgumentException("Username, old password and new password must not be blank");
        }
        validateUser(username, oldPassword);
        authenticationDao.changePasswordForUsername(username, newPassword);
    }

}
