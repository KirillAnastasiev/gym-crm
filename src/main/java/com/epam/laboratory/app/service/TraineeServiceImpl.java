package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.TraineeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class TraineeServiceImpl extends AbstractUserService<Trainee> implements TraineeService {

    public TraineeServiceImpl(@Autowired TraineeDao traineeDao,
                              @Autowired AuthenticationService authenticationService) {
        super(traineeDao, authenticationService);
    }

    @Logging(INFO)
    @Override
    public Trainee update(Trainee entity) {
        var updatedTrainee = super.update(entity);
        updatedTrainee.getTrainers();
        return updatedTrainee;
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Trainee selectByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
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
            throw new IllegalArgumentException("Username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
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
        ((TraineeDao) dao).changeStatus(username, isActive);
    }
}
