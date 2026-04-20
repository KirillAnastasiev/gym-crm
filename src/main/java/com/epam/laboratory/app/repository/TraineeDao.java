package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;

import java.util.Optional;

public interface TraineeDao extends Dao<Trainee> {
    Optional<Trainee> findByUsername(String username);
    boolean existsByUsername(String username);
    long calculateTraineesWithFirstNameAndLastName(String firstName, String lastName);
}
