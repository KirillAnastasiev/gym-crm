package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TraineeDaoImpl extends AbstractDao<Trainee> implements TraineeDao {

    @Autowired
    public TraineeDaoImpl(Storage storage) {
        super(storage);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return findById(id, Trainee.class);
    }

    @Override
    public Collection<Trainee> findAll() {
        return findAll(Trainee.class);
    }

    @Override
    public Trainee save(Trainee trainee) {
        long id = save(trainee, Trainee.class);
        trainee.setId(id);

        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        return update(trainee, trainee.getId(), Trainee.class);
    }

    @Override
    public void delete(Trainee trainee) {
        delete(trainee.getId(), Trainee.class);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return storage.values()
                .stream()
                .filter(Trainee.class::isInstance)
                .map(Trainee.class::cast)
                .filter(trainee -> trainee.getUsername().equals(username))
                .findFirst();
    }
}
