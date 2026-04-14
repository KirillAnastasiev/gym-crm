package com.epam.laboratory.app.dao;

import java.util.Collection;
import java.util.Optional;

public interface BaseDao<T, K> {
    Optional<T> findById(K id);
    Collection<T> findAll();
    T save(T entity);
    T update(T entity);
    void delete(T entity);
}
