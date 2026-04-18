package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Training;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TrainingDaoImpl extends AbstractDao<Training> implements TrainingDao {

    @Autowired
    public TrainingDaoImpl(Storage storage) {
        super(storage);
    }

    @Logging(Level.INFO)
    @Override
    public Optional<Training> findById(Long id) {
        return findById(id, Training.class);
    }

    @Logging(Level.INFO)
    @Override
    public Collection<Training> findAll() {
        return findAll(Training.class);
    }

    @Logging(Level.INFO)
    @Override
    public Training save(Training training) {
        long id = save(training, Training.class);
        training.setId(id);

        return training;
    }

    @Logging(Level.INFO)
    @Override
    public Training update(Training training) {
        return update(training, training.getId(), Training.class);
    }

    @Logging(Level.INFO)
    @Override
    public void delete(Training training) {
        delete(training.getId(), Training.class);
    }

    @Logging(Level.INFO)
    @Override
    public Optional<Training> findByTrainingName(String trainingName) {
        return storage.values()
                .stream()
                .filter(Training.class::isInstance)
                .map(Training.class::cast)
                .filter(training -> training.getTrainingName().equals(trainingName))
                .findFirst();
    }
}
