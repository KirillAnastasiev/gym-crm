package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Entity;

import java.util.Collection;
import java.util.Optional;

public interface Dao<T extends Entity> {
    Optional<T> findById(Long id);
    Collection<T> findAll();
    T save(T entity);
    T update(T entity);
    void delete(T entity);
}
