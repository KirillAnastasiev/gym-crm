package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TrainingDao;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingDao trainingDao;

    @Override
    public Training createTraining(Training training) {
        return trainingDao.save(training);
    }

    @Override
    public Training updateTraining(Training training) {
        return trainingDao.update(training);
    }

    @Override
    public void deleteTraining(Training training) {
        trainingDao.delete(training);
    }

    @Override
    public Training selectTraining(String trainingName) {
        return trainingDao.findByTrainingName(trainingName)
                .orElseThrow(() -> new NoSuchEntityException("Training with training name " + trainingName + " not found"));
    }

    @Override
    public Collection<Training> selectAllTrainings() {
        return trainingDao.findAll();
    }
}
