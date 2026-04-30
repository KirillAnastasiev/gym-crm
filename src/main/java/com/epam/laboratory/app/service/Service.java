package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Entity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

public interface Service<T extends Entity> {
    Optional<T> selectById(Long id, Class<T> entityClass);
    Collection<T> selectByCondition(BiFunction<CriteriaBuilder, Root<T>, Predicate> condition, Class<T> entityClass);
    T create(T entity);
    T update(T entity);
    void delete(T entity);

    @SafeVarargs
    static <T extends Entity>BiFunction<CriteriaBuilder, Root<T>, Predicate> conditionJoiner(BiFunction<CriteriaBuilder, Root<T>, Predicate>... conditions) {
        return (cb, root) -> {
            var totalPredicat = cb.conjunction();
            for (var condition : conditions) {
                totalPredicat = cb.and(totalPredicat, condition.apply(cb, root));
            }
            return totalPredicat;
        };
    }

    static <T extends Entity> BiFunction<CriteriaBuilder, Root<T>, Predicate> and(BiFunction<CriteriaBuilder, Root<T>, Predicate> first, BiFunction<CriteriaBuilder, Root<T>, Predicate> second) {
        return (cb, root) -> cb.and(first.apply(cb, root), second.apply(cb, root));
    }

    static <T extends Entity> BiFunction<CriteriaBuilder, Root<T>, Predicate> or(BiFunction<CriteriaBuilder, Root<T>, Predicate> first, BiFunction<CriteriaBuilder, Root<T>, Predicate> second) {
        return (cb, root) -> cb.or(first.apply(cb, root), second.apply(cb, root));
    }
    static <T extends Entity> BiFunction<CriteriaBuilder, Root<T>, Predicate> not(BiFunction<CriteriaBuilder, Root<T>, Predicate> condition) {
        return (cb, root) -> cb.not(condition.apply(cb, root));
    }

}
