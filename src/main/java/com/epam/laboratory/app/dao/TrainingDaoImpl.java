package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TrainingDaoImpl extends BaseDaoImpl<Training, Long> implements TrainingDao {

    @Autowired
    public TrainingDaoImpl(Storage storage) {
        super(storage);
    }

    @Override
    public Optional<Training> findById(Long id) {
        // todo
        return null;
    }

    @Override
    public Collection<Training> findAll() {
        // todo
        return null;
    }

    @Override
    public Training save(Training training) {
        // todo
        return null;
    }

    @Override
    public Training update(Training entity) {
        // todo
        return null;
    }

    @Override
    public void delete(Training entity) {
        // todo
    }

    @Override
    public Optional<Training> findByTrainingName(String trainingName) {
        // todo
        return null;
    }

}
