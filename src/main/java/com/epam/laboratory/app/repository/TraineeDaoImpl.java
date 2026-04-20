package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TraineeDaoImpl extends AbstractDao<Trainee> implements TraineeDao {

    public TraineeDaoImpl(@Autowired Storage storage) {
        super(storage);
    }
}