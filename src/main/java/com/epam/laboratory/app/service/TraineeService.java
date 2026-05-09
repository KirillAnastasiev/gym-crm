package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public interface TraineeService extends UserService<Trainee> {
    Collection<Trainer> updateTrainers(String traineeUsername, Collection<Trainer> trainers);

    static BiFunction<CriteriaBuilder, Root<Trainee>, Predicate> byUsernames(String... username) {
        return (cb, root) -> cb.and(root.get("username").in(List.of(username)));
    }
    static BiFunction<CriteriaBuilder, Root<Trainee>, Predicate> byTrainerUsernames(String... username) {
        return (cb, root) -> cb.and(root.get("trainers").get("username").in(List.of(username)));
    }

    static BiFunction<CriteriaBuilder, Root<Trainee>, Predicate> dateOfBirthFrom(LocalDate date) {
        return (cb, root) -> cb.greaterThanOrEqualTo(root.get("dateOfBirth"), date);
    }

    static BiFunction<CriteriaBuilder, Root<Trainee>, Predicate> dateOfBirthTo(LocalDate date) {
        return (cb, root) -> cb.lessThanOrEqualTo(root.get("dateOfBirth"), date);
    }

}
