package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.service.TrainingService;

public interface TrainingDao {
    TrainingService save(TrainingService trainingService);
    TrainingService findById(long id);
}
