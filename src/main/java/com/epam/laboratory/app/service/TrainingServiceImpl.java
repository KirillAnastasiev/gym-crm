package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TrainingDao;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingDao trainingDao;

    @Override
    public Training creteTraining(Training training) {
        return trainingDao.save(training);
    }

    @Override
    public Training selectTraining(String id) {
        return trainingDao.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("Training not found"));
    }
}
