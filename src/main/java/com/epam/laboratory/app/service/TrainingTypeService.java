package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.TrainingType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.function.BiFunction;

public interface TrainingTypeService extends Service<TrainingType> {

    static BiFunction<CriteriaBuilder, Root<TrainingType>, Predicate> byTrainingTypeNames(String... trainingTypeName) {
        return (cb, root) -> cb.and(root.get("trainingTypeName").in(List.of(trainingTypeName)));
    }

}
