package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainer;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDaoImpl implements TrainerDao {
    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Collection<Trainer> findAll() {
        return List.of();
    }

    @Override
    public Trainer save(Trainer trainee) {
        // todo
        return null;
    }

    @Override
    public Trainer update(Trainer trainee) {
        // todo
        return null;
    }

    @Override
    public void delete(Trainer trainee) {
        // todo
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return Optional.empty();
    }
}
