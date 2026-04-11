package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TrainerDao;
import com.epam.laboratory.app.domain.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerDao trainerDao;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        return trainerDao.save(trainer);
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        return trainerDao.update(trainer);
    }

    @Override
    public Trainer selectTrainer(Long id) {
        return trainerDao.findById(id);
    }
}
