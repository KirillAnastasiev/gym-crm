package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeDao traineeDao;
    private final PasswordGenerator passwordGenerator;
    private final UsernameHelper usernameHelper;

    @Logging(Level.INFO)
    @Override
    public Trainee createTrainee(Trainee trainee) {
        trainee.setPassword(passwordGenerator.generatePassword());
        trainee.setUsername(getUsername(trainee));

        return traineeDao.save(trainee);
    }

    @Logging(Level.INFO)
    @Override
    public Trainee updateTrainee(Trainee trainee) {
        trainee.setUsername(getUsername(trainee));

        return traineeDao.update(trainee);
    }

    @Logging(Level.INFO)
    @Override
    public void deleteTrainee(Trainee trainee) {
        traineeDao.delete(trainee);
    }

    @Logging(Level.INFO)
    @Override
    public Collection<Trainee> selectTraineesByCondition(Predicate<Trainee> condition) {
        return traineeDao.findByCondition(condition, Trainee.class);
    }

    @Logging(Level.INFO)
    @Override
    public Collection<Trainee> selectAllTrainees() {
        return traineeDao.findAll();
    }

    private String getUsername(Trainee trainee) {
        Collection<Trainee> traineesWithSameFirstNameAndLastName = traineeDao.findByCondition(
                t -> t.getFirstName().equals(trainee.getFirstName())
                        && t.getLastName().equals(trainee.getLastName()), Trainee.class);
        return usernameHelper.generateUsername(trainee.getFirstName(), trainee.getLastName(), traineesWithSameFirstNameAndLastName);
    }
}
