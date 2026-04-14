package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainer;

public interface TrainerService {
    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Trainer trainer);
    Trainer selectTrainer(String id);
}
