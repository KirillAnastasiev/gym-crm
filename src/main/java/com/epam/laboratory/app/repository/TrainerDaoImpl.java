package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainer;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TrainerDaoImpl extends AbstractDao<Trainer> implements TrainerDao {

//    @Autowired
    public TrainerDaoImpl(@Autowired Storage storage) {
        super(storage);
    }

    @Logging(Level.INFO)
    @Override
    public Collection<Trainer> findAll() {
        return findAll(Trainer.class);
    }

    @Logging(Level.INFO)
    @Override
    public Optional<Trainer> findByUsername(String username) {
        return storage.values()
                .stream()
                .filter(Trainer.class::isInstance)
                .map(Trainer.class::cast)
                .filter(trainer -> trainer.getUsername().equals(username))
                .findFirst();
    }

    @Logging(Level.INFO)
    @Override
    public boolean existsByUsername(String username) {
        return storage.values()
                .stream()
                .filter(Trainer.class::isInstance)
                .map(Trainer.class::cast)
                .anyMatch(trainer -> trainer.getUsername().equals(username));
    }

    @Logging(Level.INFO)
    @Override
    public long calculateTrainersWithFirstNameAndLastName(String firstName, String lastName) {
        return storage.values()
                .stream()
                .filter(Trainer.class::isInstance)
                .map(Trainer.class::cast)
                .filter(trainer -> trainer.getFirstName().equals(firstName) && trainer.getLastName().equals(lastName))
                .count();
    }
}
