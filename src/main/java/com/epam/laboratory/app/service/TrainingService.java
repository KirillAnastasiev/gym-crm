package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Training;

import java.util.Collection;

public interface TrainingService {
    Training createTraining(Training training);
    Training updateTraining(Training training);
    void deleteTraining(Training training);
    Training selectTraining(String id);
    Collection<Training> selectAllTrainings();
}
