package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TrainerDao;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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
    public void deleteTrainer(Trainer trainer) {
        trainerDao.delete(trainer);
    }

    @Override
    public Trainer selectTrainer(String username) {
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchEntityException("Trainer with username " + username + " not found"));
    }

    @Override
    public Collection<Trainer> selectAllTrainees() {
        return trainerDao.findAll();
    }
}
