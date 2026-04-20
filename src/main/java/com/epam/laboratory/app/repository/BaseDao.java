package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Entity;

import java.util.Collection;
import java.util.Optional;

public interface BaseDao<T extends Entity, K> {
    Optional<T> findById(K id);
    Collection<T> findAll();
    T save(T entity);
    T update(T entity);
    void delete(T entity);
}
