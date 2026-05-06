package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;

public interface TrainingService extends EntityService<Training> {

    static BiFunction<CriteriaBuilder, Root<Training>, Predicate> byTrainerUsernames(String... username) {
        return (cb, root) -> cb.and(root.get("trainer").get("username").in(List.of(username)));
    }

    static  BiFunction<CriteriaBuilder, Root<Training>, Predicate> byTraineeUsernames(String... username) {
        return (cb, root) -> cb.and(root.get("trainee").get("username").in(List.of(username)));
    }

    static BiFunction<CriteriaBuilder, Root<Training>, Predicate> byTrainingTypeNames(String... trainingTypeNames) {
        return (cb, root) -> cb.and(root.get("trainingType").get("trainingTypeName").in(List.of(trainingTypeNames)));
    }

    static BiFunction<CriteriaBuilder, Root<Training>, Predicate> fromDate(LocalDateTime fromDate) {
        return (cb, root) -> cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate);
    }

    static  BiFunction<CriteriaBuilder, Root<Training>, Predicate> toDate(LocalDateTime toDate) {
        return (cb, root) -> cb.lessThanOrEqualTo(root.get("trainingDate"), toDate);
    }

    static BiFunction<CriteriaBuilder, Root<Training>, Predicate> byDuration(Duration trainingDuration) {
        return (cb, root) -> cb.equal(root.get("trainingDuration"), trainingDuration);
    }

}
