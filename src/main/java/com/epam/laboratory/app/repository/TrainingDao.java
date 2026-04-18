package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Training;

import java.util.Optional;

public interface TrainingDao extends  BaseDao<Training, Long> {
    Optional<Training> findByTrainingName(String trainingName);
}
