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

    public TraineeDaoImpl(@Autowired Storage storage) {
        super(storage);
    }

    @Logging(Level.INFO)
    @Override
    public Collection<Trainee> findAll() {
        return findAll(Trainee.class);
    }
}