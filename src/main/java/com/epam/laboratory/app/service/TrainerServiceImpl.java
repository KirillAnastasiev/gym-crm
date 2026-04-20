package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerDao trainerDao;
    private final PasswordGenerator passwordGenerator;
    private final UsernameHelper usernameHelper;

    @Logging(Level.INFO)
    @Override
    public Trainer createTrainer(Trainer trainer) {
        trainer.setPassword(getPassword());
        trainer.setUsername(getUsername(trainer));

        return trainerDao.save(trainer);
    }

    @Logging(Level.INFO)
    @Override
    public Trainer updateTrainer(Trainer trainer) {
        return trainerDao.update(trainer);
    }

    @Logging(Level.INFO)
    @Override
    public void deleteTrainer(Trainer trainer) {
        trainerDao.delete(trainer);
    }

    @Logging(Level.INFO)
    @Override
    public Trainer selectTrainer(String username) {
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchEntityException("Trainer with username " + username + " not found"));
    }

    @Logging(Level.INFO)
    @Override
    public Collection<Trainer> selectAllTrainees() {
        return trainerDao.findAll();
    }

    private String getPassword() {
        return passwordGenerator.generatePassword();
    }

    private String getUsername(Trainer trainer) {
        var username = usernameHelper.generateUsername(trainer.getFirstName(), trainer.getLastName());
        var isAlreadyExists = trainerDao.existsByUsername(username);
        if (isAlreadyExists) {
            long traineesCount = trainerDao.calculateTrainersWithFirstNameAndLastName(trainer.getFirstName(), trainer.getLastName());
            username = usernameHelper.generateUsername(trainer.getFirstName(), trainer.getLastName(), String.valueOf(traineesCount + 1));
        }

        return username;
    }
}
