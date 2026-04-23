package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Entity;

import java.util.Collection;
import java.util.function.Predicate;

public interface Service<T extends Entity> {
    T create(T entity);
    T update(T entity);
    void delete(T entity);
    Collection<T> selectByCondition(Predicate<T> condition, Class<T> entityClass);
}
