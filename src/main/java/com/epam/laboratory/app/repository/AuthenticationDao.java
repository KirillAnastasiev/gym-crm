package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.User;

import java.util.Optional;

public interface AuthenticationDao {
    boolean checkExistsByUsername(String username);
    boolean checkPasswordForUsername(String userName, String password);
    void changePasswordForUsername(String username, String newPassword);
    Optional<User> findUserByUsername(String username);
}
