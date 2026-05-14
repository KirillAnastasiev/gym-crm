package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.TraineeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class TraineeServiceImpl extends AbstractUserService<Trainee> implements TraineeService {

    private final TrainerService trainerService;

    @Autowired
    public TraineeServiceImpl(TraineeDao traineeDao,
                              AuthenticationService authenticationService,
                              @Lazy TrainerService trainerService) {
        super(traineeDao, authenticationService);
        this.trainerService = trainerService;
    }

    @Logging(INFO)
    @Override
    public Trainee updateByUsername(String username, Trainee entity) {
        if (username == null) {
            throw new IllegalArgumentException("Trainee username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Trainee username must not be blank");
        }
        if (entity == null) {
            throw new IllegalArgumentException("Trainee must not be null");
        }
        var updatedTrainee = ((TraineeDao) dao).updateByUsername(username, entity);
        updatedTrainee.getTrainers();
        return updatedTrainee;
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Trainee selectByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Trainee username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Trainee username must not be blank");
        }
        var optionalTrainee = ((TraineeDao) dao).findByUsername(username);
        var trainee = optionalTrainee.orElseThrow(() ->
                new NoSuchEntityException("Trainee with username " + username + " not found"));
        trainee.getTrainers();
        return trainee;
    }

    @Logging(INFO)
    @Override
    public void deleteByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Trainee username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Trainee username must not be blank");
        }
        var isExists = authenticationService.checkExistsByUsername(username);
        if (!isExists) {
            throw new NoSuchEntityException("Trainee with username " + username + " not found");
        }
        ((TraineeDao) dao).deleteByUsername(username);
    }

    @Logging(INFO)
    @Override
    public void changeStatus(String username, boolean isActive) {
        var isExists = authenticationService.checkExistsByUsername(username);
        if (!isExists) {
            throw new NoSuchEntityException("Trainee with username " + username + " not found");
        }
        ((TraineeDao) dao).changeStatusByUsername(username, isActive);
    }

    @Logging(INFO)
    @Override
    public Collection<Trainer> updateTrainers(String traineeUsername, Collection<Trainer> trainers) {
        var trainee = selectByUsername(traineeUsername);
        List<Trainer> trainersToSet = trainers.stream()
                .map(Trainer::getUsername)
                .map(trainerService::selectByUsername)
                .toList();

        trainee.getTrainers();
        trainee.addTrainers(trainersToSet);
        var updatedTrainee = update(trainee);
        return updatedTrainee.getTrainers();
    }

}
