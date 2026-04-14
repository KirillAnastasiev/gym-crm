package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainer;

import java.util.Collection;

public interface TrainerService {
    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Trainer trainer);
    void deleteTrainer(Trainer trainer);
    Trainer selectTrainer(String id);
    Collection<Trainer> selectAllTrainees();
}
