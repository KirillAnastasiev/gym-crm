package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.TrainingType;

import java.util.Collection;
import java.util.Optional;

public interface TrainingTypeDao extends EntityDao<TrainingType> {
    Collection<TrainingType> findAll();
    Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);
}
