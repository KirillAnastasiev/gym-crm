package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.repository.TrainingDao;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingDao trainingDao;

    @Logging(Level.INFO)
    @Override
    public Training createTraining(Training training) {
        return trainingDao.save(training);
    }

    @Logging(Level.INFO)
    @Override
    public Training updateTraining(Training training) {
        return trainingDao.update(training);
    }

    @Logging(Level.INFO)
    @Override
    public void deleteTraining(Training training) {
        trainingDao.delete(training);
    }

    @Logging(Level.INFO)
    @Override
    public Collection<Training> selectTrainingsByCondition(Predicate<Training> condition) {
        return trainingDao.findByCondition(condition, Training.class);
    }
}
