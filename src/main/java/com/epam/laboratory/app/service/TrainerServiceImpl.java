package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerDao trainerDao;
    private final UsernameHelper usernameHelper;

    @Logging(Level.INFO)
    @Override
    public Trainer createTrainer(Trainer trainer) {
        trainer.setPassword(PasswordGenerator.generatePassword());
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
    public Collection<Trainer> selectTrainerByCondition(Predicate<Trainer> condition) {
        return trainerDao.findByCondition(condition, Trainer.class);
    }

    private String getUsername(Trainer trainer) {
        Collection<Trainer> trainersWithSameFirstNameAndLastName = trainerDao.findByCondition(
                t -> t.getFirstName().equals(trainer.getFirstName())
                        && t.getLastName().equals(trainer.getLastName()), Trainer.class);
        return usernameHelper.generateUsername(trainer.getFirstName(), trainer.getLastName(), trainersWithSameFirstNameAndLastName);
    }
}
