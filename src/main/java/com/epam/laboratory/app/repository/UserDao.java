package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.User;

import java.util.Optional;

public interface UserDao<T extends User> extends Dao<T> {
    Optional<T> findByUsername(String username);
    void changePassword(Long id, String newPassword);
    void changeStatus(Long id, boolean isActive);
    void deleteByUsername(String username);
    boolean existsByUsername(String username);
}
