package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class TrainerServiceImpl extends AbstractUserService<Trainer> implements TrainerService {

    private final TraineeService traineeService;

    public TrainerServiceImpl(@Autowired TrainerDao trainerDao,
                              @Autowired AuthenticationService authenticationService,
                              @Autowired TraineeService traineeService) {
        super(trainerDao, authenticationService);
        this.traineeService = traineeService;
    }

    @Logging(INFO)
    @Override
    public Trainer update(Trainer entity) {
        var updatedTrainer = super.update(entity);
        updatedTrainer.getTrainees();
        return updatedTrainer;
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Trainer selectByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Trainer username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Trainer username must not be blank");
        }
        var optionalTrainer = ((TrainerDao) dao).findByUsername(username);
        var trainer = optionalTrainer.orElseThrow(() ->
                new NoSuchEntityException("Trainer with username " + username + " not found"));
        trainer.getTrainees();
        return trainer;
    }

    @Logging(INFO)
    @Override
    public void deleteByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Trainer username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Trainer username must not be blank");
        }
        var isExists = authenticationService.checkExistsByUsername(username);
        if (!isExists) {
            throw new NoSuchEntityException("Trainer with username " + username + " not found");
        }
        ((TrainerDao) dao).deleteByUsername(username);
    }

    @Logging(INFO)
    @Override
    public void changeStatus(String username, boolean isActive) {
        var isExists = authenticationService.checkExistsByUsername(username);
        if (!isExists) {
            throw new NoSuchEntityException("Trainer with username " + username + " not found");
        }
        ((TrainerDao) dao).changeStatusByUsername(username, isActive);
    }

    @Logging(INFO)
    @Override
    public Collection<Trainee> updateTrainees(String trainerUsername, Collection<Trainee> trainees) {
        var trainer = selectByUsername(trainerUsername);
        List<Trainee> traineesToSet = trainees.stream()
                .map(Trainee::getUsername)
                .map(traineeService::selectByUsername)
                .toList();

        trainer.getTrainees();
        trainer.addTrainees(traineesToSet);
        var updatedTrainer = update(trainer);
        return updatedTrainer.getTrainees();
    }
}
