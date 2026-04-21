package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Entity;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public interface Dao<T extends Entity> {
    Optional<T> findById(Long id, Class<T> clazz);
    Collection<T> findByCondition(Predicate<T> condition, Class<T> clazz);
    T save(T entity);
    T update(T entity);
    void delete(T entity);
}
