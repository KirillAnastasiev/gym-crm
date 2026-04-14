package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;

import java.util.Collection;

public interface TraineeService {
    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Trainee trainee);
    void deleteTrainee(Trainee trainee);
    Trainee selectTrainee(String username);
    Collection<Trainee> selectAllTrainees();
}
