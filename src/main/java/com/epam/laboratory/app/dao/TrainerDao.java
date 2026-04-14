package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainer;

import java.util.Optional;

public interface TrainerDao extends BaseDao<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
}
