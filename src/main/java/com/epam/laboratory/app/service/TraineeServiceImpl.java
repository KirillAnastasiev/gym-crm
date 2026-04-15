package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TraineeDao;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeDao traineeDao;
    private final PasswordGenerator passwordGenerator;
    private final UsernameHelper usernameHelper;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        trainee.setPassword(getPassword());
        trainee.setUsername(getUsername(trainee));

        return traineeDao.save(trainee);
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        trainee.setUsername(getUsername(trainee));

        return traineeDao.update(trainee);
    }

    @Override
    public void deleteTrainee(Trainee trainee) {
        traineeDao.delete(trainee);
    }

    @Override
    public Trainee selectTrainee(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchEntityException("Trainee with username " + username + " not found"));
    }

    @Override
    public Collection<Trainee> selectAllTrainees() {
        return traineeDao.findAll();
    }

    private String getPassword() {
        return passwordGenerator.generatePassword();
    }

    private String getUsername(Trainee trainee) {
        var username = usernameHelper.generateUsername(trainee);
        var isAlreadyExists = traineeDao.existsByUsername(username);
        if (isAlreadyExists) {
            long traineesCount = traineeDao.calculateTraineesWithFirstNameAndLastName(trainee.getFirstName(), trainee.getLastName());
            username = usernameHelper.generateUsername(trainee, String.valueOf(traineesCount + 1));
        }

        return username;
    }
}
