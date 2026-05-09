package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.User;

import java.util.Optional;

public interface UserDao<T extends User> extends EntityDao<T> {
    Optional<T> findByUsername(String username);
    void changeStatusByUsername(String username, boolean isActive);
    void deleteByUsername(String username);
}
