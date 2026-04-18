package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TrainerDaoImpl extends AbstractDao<Trainer> implements TrainerDao {

    @Autowired
    public TrainerDaoImpl(Storage storage) {
        super(storage);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return findById(id, Trainer.class);
    }

    @Override
    public Collection<Trainer> findAll() {
        return findAll(Trainer.class);
    }

    @Override
    public Trainer save(Trainer trainer) {
        long id = save(trainer, Trainer.class);
        trainer.setId(id);

        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        return update(trainer, trainer.getId(), Trainer.class);
    }

    @Override
    public void delete(Trainer trainee) {
        delete(trainee.getId(), Trainer.class);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return storage.values()
                .stream()
                .filter(Trainer.class::isInstance)
                .map(Trainer.class::cast)
                .filter(trainer -> trainer.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return storage.values()
                .stream()
                .filter(Trainer.class::isInstance)
                .map(Trainer.class::cast)
                .anyMatch(trainer -> trainer.getUsername().equals(username));
    }

    @Override
    public long calculateTraineesWithFirstNameAndLastName(String firstName, String lastName) {
        return storage.values()
                .stream()
                .filter(Trainer.class::isInstance)
                .map(Trainer.class::cast)
                .filter(trainer -> trainer.getFirstName().equals(firstName) && trainer.getLastName().equals(lastName))
                .count();
    }
}
