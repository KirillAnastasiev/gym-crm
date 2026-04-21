package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.User;
import com.epam.laboratory.app.repository.Dao;

public abstract class UserService<T extends User> extends AbstractService<T> {
    public UserService(Dao<T> dao) {
        super(dao);
    }

    @Override
    public T create(T user) {
        prepareUser(user);
        return super.create(user);
    }

    @Override
    public T update(T user) {
        prepareUser(user);
        return super.update(user);
    }

    protected abstract void prepareUser(T user);
}
