package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerDaoImpl extends AbstractDao<Trainer> implements TrainerDao {

    public TrainerDaoImpl(@Autowired Storage storage) {
        super(storage);
    }
}
