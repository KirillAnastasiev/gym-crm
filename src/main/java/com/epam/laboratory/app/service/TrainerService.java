package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainer;

import java.util.Collection;
import java.util.function.Predicate;

public interface TrainerService {
    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Trainer trainer);
    void deleteTrainer(Trainer trainer);
    Collection<Trainer> selectTrainerByCondition(Predicate<Trainer> condition);
}
