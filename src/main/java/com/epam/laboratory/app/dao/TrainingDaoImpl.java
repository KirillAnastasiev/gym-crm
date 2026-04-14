package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDaoImpl extends BaseDaoImpl<Training, Long> implements TrainingDao {
    private static final String KEY_PREFIX = "training:";

    @Autowired
    public TrainingDaoImpl(Map<String, Object> storage) {
        super(storage);
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Collection<Training> findAll() {
        return List.of();
    }

    @Override
    public Training save(Training training) {
        return null;
    }

    @Override
    public Training update(Training entity) {
        return null;
    }

    @Override
    public void delete(Training entity) {

    }

    @Override
    public Optional<Training> findByTrainingName(String trainingName) {
        return null;
    }

}
