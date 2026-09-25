package com.kirill.projects.gymcrm.app.service.security;

import com.kirill.projects.gymcrm.app.aspect.annotation.Logging;
import com.kirill.projects.gymcrm.app.domain.User;
import com.kirill.projects.gymcrm.app.domain.UserSecurity;
import com.kirill.projects.gymcrm.app.exception.NoSuchEntityException;
import com.kirill.projects.gymcrm.app.repository.AuthenticationDao;
import com.kirill.projects.gymcrm.app.util.InputDataValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class UserSecurityServiceImpl implements UserSecurityService {
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final int LOCK_TIME_DURATION_SECONDS = 300;

    private final AuthenticationDao authenticationDao;
    private final JwtService jwtService;

    @Logging(Level.INFO)
    @Override
    public Optional<User> getUserWithUserSecurityByUsername(String username) {
        InputDataValidator.validateNotBlank(username, "Username");
        return authenticationDao.findUserByUsername(username, true);
    }

    @Logging(Level.INFO)
    @Override
    public void checkUserSecurity(User user) {
        InputDataValidator.validateNotNull(user, "User");
        var security = user.getSecurity();
        if (security == null) {
            return;
        }
        if (security.isAccountLocked() && isUserLockTimeExpired(user, LOCK_TIME_DURATION_SECONDS)) {
            unlockUser(user);
        }
        if (security.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            lockUser(user);
        }
    }

    @Logging(Level.INFO)
    @Override
    public void increaseFailedAttempts(String username) {
        InputDataValidator.validateNotBlank(username, "Username");
        var user = authenticationDao.findUserByUsername(username, true)
                .orElseThrow(() -> new NoSuchEntityException("User with username " + username + " not found"));
        var security = user.getSecurity();
        if (security == null) {
            security = new UserSecurity();
            security.setUser(user);
            user.setSecurity(security);
        }
        security.setFailedAttempts(security.getFailedAttempts() + 1);
        authenticationDao.save(user);
    }

    @Logging(Level.INFO)
    @Override
    public void resetFailedAttempts(String username) {
        InputDataValidator.validateNotBlank(username, "Username");
        var user = authenticationDao.findUserByUsername(username, true)
                .orElseThrow(() -> new NoSuchEntityException("User with username " + username + " not found"));
        var security = user.getSecurity();
        if (security != null) {
            security.setFailedAttempts(0);
            authenticationDao.save(user);
        }
    }

    @Logging(Level.INFO)
    @Override
    public void lockUser(User user) {
        InputDataValidator.validateNotNull(user, "User");
        var security = user.getSecurity();
        if (security == null) {
            security = new UserSecurity();
            security.setUser(user);
            user.setSecurity(security);
        }
        security.setAccountLocked(true);
        security.setLockTime(Instant.now());
        authenticationDao.save(user);
        jwtService.revokeTokens(user.getUsername());
    }

    @Logging(Level.INFO)
    @Override
    public void unlockUser(User user) {
        InputDataValidator.validateNotNull(user, "User");
        var security = user.getSecurity();
        security.setAccountLocked(false);
        security.setLockTime(null);
        security.setFailedAttempts(0);
        authenticationDao.save(user);
    }

    @Logging(Level.INFO)
    @Override
    public boolean isUserLockTimeExpired(User user, int lockTimeSeconds) {
        InputDataValidator.validateNotNull(user, "User");
        var security = user.getSecurity();
        if (security == null || security.getLockTime() == null) {
            return false;
        }
        return Instant.now().isAfter(security.getLockTime().plusSeconds(lockTimeSeconds));
    }

}
