package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;

import java.util.Collection;
import java.util.function.Predicate;

public interface TraineeService {
    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Trainee trainee);
    void deleteTrainee(Trainee trainee);
    Collection<Trainee> selectTraineesByCondition(Predicate<Trainee> condition);
}
