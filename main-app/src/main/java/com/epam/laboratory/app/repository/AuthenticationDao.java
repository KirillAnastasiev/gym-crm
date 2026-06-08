package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.User;

import java.util.Optional;

public interface AuthenticationDao {
    boolean checkExistsByUsername(String username);
    void changePasswordForUsername(String username, String newPassword);
    Optional<User> findUserByUsername(String username, boolean withSecurity);
    void save(User user);
}
