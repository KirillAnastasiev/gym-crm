package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainee;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TraineeDaoImpl extends AbstractDao<Trainee> implements TraineeDao {

//    @Autowired
    public TraineeDaoImpl(@Autowired Storage storage) {
        super(storage);
    }

    @Logging(Level.INFO)
    @Override
    public Collection<Trainee> findAll() {
        return findAll(Trainee.class);
    }

    @Logging(Level.INFO)
    @Override
    public Optional<Trainee> findByUsername(String username) {
        return storage.values()
                .stream()
                .filter(Trainee.class::isInstance)
                .map(Trainee.class::cast)
                .filter(trainee -> trainee.getUsername().equals(username))
                .findFirst();
    }

    @Logging(Level.INFO)
    @Override
    public boolean existsByUsername(String username) {
        return storage.values()
                .stream()
                .filter(Trainee.class::isInstance)
                .map(Trainee.class::cast)
                .anyMatch(trainee -> trainee.getUsername().equals(username));
    }

    @Logging(Level.INFO)
    @Override
    public long calculateTraineesWithFirstNameAndLastName(String firstName, String lastName) {
        return storage.values()
                .stream()
                .filter(Trainee.class::isInstance)
                .map(Trainee.class::cast)
                .filter(trainee -> trainee.getFirstName().equals(firstName) && trainee.getLastName().equals(lastName))
                .count();
    }
}
