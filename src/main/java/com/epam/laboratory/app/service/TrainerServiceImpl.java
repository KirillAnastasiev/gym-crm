package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TrainerDao;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerDao trainerDao;
    private final PasswordGenerator passwordGenerator;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        var password = passwordGenerator.generatePassword();
        trainer.setPassword(password);

        return trainerDao.save(trainer);
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        return trainerDao.update(trainer);
    }

    @Override
    public Trainer selectTrainer(Long id) {
        return trainerDao.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("Trainer with id " + id + " not found"));
    }
}
