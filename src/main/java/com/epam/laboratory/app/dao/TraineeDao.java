package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;

import java.util.Optional;

public interface TraineeDao extends BaseDao<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);
    boolean existsByUsername(String username);
    long calculateTraineesWithFirstNameAndLastName(String firstName, String lastName);
}
