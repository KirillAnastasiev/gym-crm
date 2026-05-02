package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.repository.TrainingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingServiceImpl extends AbstractService<Training> implements TrainingService {

    public TrainingServiceImpl(@Autowired TrainingDao trainingDao) {
        super(trainingDao);
    }

    @Override
    protected void prepareEntity(Training entity) {
        // dumb method
    }

}
