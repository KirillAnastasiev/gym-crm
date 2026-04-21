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

    public TrainerDaoImpl(@Autowired Storage storage) {
        super(storage);
    }
}
