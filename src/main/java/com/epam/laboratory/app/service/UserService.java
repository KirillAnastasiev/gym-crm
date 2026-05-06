package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.User;

public interface UserService<T extends User> extends EntityService<T> {
    T selectByUsername(String username);
    void changeStatus(String username, boolean isActive);
    void deleteByUsername(String username);
}
