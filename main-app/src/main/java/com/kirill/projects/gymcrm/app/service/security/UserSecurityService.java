package com.kirill.projects.gymcrm.app.service.security;

import com.kirill.projects.gymcrm.app.domain.User;

import java.util.Optional;

public interface UserSecurityService {
    Optional<User> getUserWithUserSecurityByUsername(String username);
    void checkUserSecurity(User user);
    void increaseFailedAttempts(String username);
    void resetFailedAttempts(String username);
    void lockUser(User user);
    void unlockUser(User user);
    boolean isUserLockTimeExpired(User user, int lockTimeSeconds);
}
