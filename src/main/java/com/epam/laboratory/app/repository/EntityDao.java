package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.BaseEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

public interface EntityDao<T extends BaseEntity> {
    Optional<T> findById(Long id, Class<T> clazz);
    Collection<T> findByCondition(BiFunction<CriteriaBuilder, Root<T>, Predicate> condition, Class<T> clazz);
    T save(T entity);
    T update(T entity);
    void delete(T entity);
}
