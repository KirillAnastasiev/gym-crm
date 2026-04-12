package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Training;

public interface TrainingService {
    Training creteTraining(Training training);
    Training selectTraining(Long id);
}
