package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;

import java.util.Optional;

public interface TraineeDao {
    Trainee save(Trainee trainee);
    Optional<Trainee> findById(String id);
    Trainee update(Trainee trainee);
    void delete(Trainee trainee);
}
