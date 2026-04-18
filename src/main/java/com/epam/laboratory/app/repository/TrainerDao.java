package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainer;

import java.util.Optional;

public interface TrainerDao extends BaseDao<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
    boolean existsByUsername(String username);
    long calculateTraineesWithFirstNameAndLastName(String firstName, String lastName);
}
