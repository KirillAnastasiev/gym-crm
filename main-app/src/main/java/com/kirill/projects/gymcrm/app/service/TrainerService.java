package com.kirill.projects.gymcrm.app.service;

import com.kirill.projects.gymcrm.app.domain.Trainee;
import com.kirill.projects.gymcrm.app.domain.Trainer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public interface TrainerService extends UserService<Trainer> {
    Collection<Trainee> updateTrainees(String trainerUsername, Collection<Trainee> trainees);
    Trainer updateByUsername(String username, Trainer trainer);

    static BiFunction<CriteriaBuilder, Root<Trainer>, Predicate> byUsernames(String... usernames) {
        return (cb, root) -> cb.and(root.get("username").in(List.of(usernames)));
    }

    static BiFunction<CriteriaBuilder, Root<Trainer>, Predicate> byTraineeUsernames(String... usernames) {
        return (cb, root) -> cb.and(root.get("trainees").get("username").in(List.of(usernames)));
    }

    static BiFunction<CriteriaBuilder, Root<Trainer>, Predicate> bySpecializations(String... specializations) {
        return (cb, root) -> cb.and(root.get("specialization").get("trainingTypeName").in(List.of(specializations)));
    }

}
