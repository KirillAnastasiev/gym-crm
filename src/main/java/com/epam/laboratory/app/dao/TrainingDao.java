package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Training;

import java.util.Optional;

public interface TrainingDao extends  BaseDao<Training, Long> {
    Optional<Training> findByTrainingName(String trainingName);
}
