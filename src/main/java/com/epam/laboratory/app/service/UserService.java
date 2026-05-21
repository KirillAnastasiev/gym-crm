package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.function.BiFunction;

public interface UserService<T extends User> extends EntityService<T> {
    T selectByUsername(String username);
    void changeStatus(String username, boolean isActive);
    void deleteByUsername(String username);

    static <T extends User> BiFunction<CriteriaBuilder, Root<T>, Predicate> byStatus(boolean isActive) {
        return (cb, root) -> cb.equal(root.get("active"), isActive);
    }
}
