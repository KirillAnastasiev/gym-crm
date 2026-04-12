package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.service.TrainingService;

import java.util.Optional;

public interface TrainingDao {
    TrainingService save(TrainingService trainingService);
    Optional<TrainingService> findById(long id);
}
