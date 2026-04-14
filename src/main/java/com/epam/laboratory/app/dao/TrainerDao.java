package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainer;

import java.util.Optional;

public interface TrainerDao {
    Trainer save(Trainer trainee);
    Optional<Trainer> findById(String id);
    Trainer update(Trainer trainee);
    void delete(Trainer trainee);
}
