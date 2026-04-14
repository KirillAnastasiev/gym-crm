package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TraineeDaoImpl extends BaseDaoImpl<Trainee, Long> implements TraineeDao {

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
        // todo
        return null;
    }

    @Override
    public Trainee save(Trainee trainee) {
        // todo
        return null;
    }

    @Override
    public Trainee update(Trainee trainee) {
        // todo
        return null;
    }

    @Override
    public void delete(Trainee trainee) {
        // todo
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        // todo
        return null;
    }
}
