package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TraineeDao;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeDao traineeDao;
    private final PasswordGenerator passwordGenerator;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        var password = passwordGenerator.generatePassword();
        trainee.setPassword(password);

        return traineeDao.save(trainee);
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        return traineeDao.update(trainee);
    }

    @Override
    public void deleteTrainee(Trainee trainee) {
        traineeDao.delete(trainee);
    }

    @Override
    public Trainee selectTrainee(Long id) {
        return traineeDao.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("Trainee with id " + id + " not found"));
    }
}
