package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDaoImpl extends BaseDaoImpl<Trainee, Long> implements TraineeDao {
    private static final String KEY_PREFIX = "trainee:";

    @Autowired
    public TraineeDaoImpl(Map<String, Object> storage) {
        super(storage);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        // todo
        return null;
    }

    @Override
    public Collection<Trainee> findAll() {
        return List.of();
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
