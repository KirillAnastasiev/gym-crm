package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.service.TrainingService;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingDaoImpl implements TrainingDao {
    @Override
    public TrainingService save(TrainingService trainingService) {
        // todo
        return null;
    }

    @Override
    public Optional<TrainingService> findById(long id) {
        // todo
        return null;
    }
}
