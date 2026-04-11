package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TrainingDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingDao trainingDao;

    @Override
    public TrainingService creteTrainingService(TrainingService trainingService) {
        return trainingDao.save(trainingService);
    }

    @Override
    public TrainingService selectTrainingService(Long id) {
        return trainingDao.findById(id);
    }
}
