package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Training;

import java.util.Collection;
import java.util.function.Predicate;

public interface TrainingService {
    Training createTraining(Training training);
    Training updateTraining(Training training);
    void deleteTraining(Training training);
    Collection<Training> selectTrainingsByCondition(Predicate<Training> condition);
}
