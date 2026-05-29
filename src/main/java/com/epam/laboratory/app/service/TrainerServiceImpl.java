package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.UserCredentials;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.service.security.AuthenticationService;
import com.epam.laboratory.app.util.InputDataValidator;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class TrainerServiceImpl extends AbstractUserService<Trainer> implements TrainerService {

    private final TraineeService traineeService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TrainerServiceImpl(TrainerDao trainerDao,
                              AuthenticationService authenticationService,
                              TraineeService traineeService,
                              PasswordEncoder passwordEncoder) {
        super(trainerDao, authenticationService);
        this.traineeService = traineeService;
        this.passwordEncoder = passwordEncoder;
    }

    @Logging(Level.INFO)
    @Override
    public UserCredentials registerNew(Trainer trainer) {
        InputDataValidator.validateNotNull(trainer, "Trainer");
        prepareUser(trainer);
        var password = trainer.getPassword();
        var encodedPassword = passwordEncoder.encode(trainer.getPassword());
        trainer.setPassword(encodedPassword);
        ((TrainerDao) dao).save(trainer);
        return new  UserCredentials(trainer.getUsername(), password);
    }

    @Logging(Level.INFO)
    @Override
    public Trainer update(Trainer trainer) {
        InputDataValidator.validateNotNull(trainer, "Trainer");
        return ((TrainerDao) dao).save(trainer);
    }

    @Logging(INFO)
    @Override
    public Trainer updateByUsername(String username, Trainer entity) {
        InputDataValidator.validateNotBlank(username, "Trainer username");
        InputDataValidator.validateNotNull(entity, "Trainer");
        var isExists = authenticationService.checkExistsByUsername(username);
        if (!isExists) {
            throw new NoSuchEntityException("Trainer with username %s not found".formatted(username));
        }
        var updatedTrainer = ((TrainerDao) dao).updateByUsername(username, entity);
        updatedTrainer.getTrainees();
        return updatedTrainer;
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Trainer selectByUsername(String username) {
        InputDataValidator.validateNotBlank(username, "Trainer username");
        var optionalTrainer = ((TrainerDao) dao).findByUsername(username);
        var trainer = optionalTrainer.orElseThrow(() ->
                new NoSuchEntityException("Trainer with username %s not found".formatted(username)));
        trainer.getTrainees();
        return trainer;
    }

    @Logging(INFO)
    @Override
    public void deleteByUsername(String username) {
        InputDataValidator.validateNotBlank(username, "Trainer username");
        var isExists = authenticationService.checkExistsByUsername(username);
        if (!isExists) {
            throw new NoSuchEntityException("Trainer with username %s not found".formatted(username));
        }
        ((TrainerDao) dao).deleteByUsername(username);
    }

    @Logging(INFO)
    @Override
    public void changeStatus(String username, boolean isActive) {
        var isExists = authenticationService.checkExistsByUsername(username);
        if (!isExists) {
            throw new NoSuchEntityException("Trainer with username %s not found".formatted(username));
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
