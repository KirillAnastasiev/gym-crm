package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.User;

import java.util.Optional;

public interface UserService<T extends User> extends Service<T> {
    Optional<T> selectByUsername(String username);
    boolean checkPasswordForUsername(String userName, String password);
    void changePassword(T user, String newPassword);
    void changeStatus(T user, boolean isActive);
    void deleteByUsername(String username);
}
