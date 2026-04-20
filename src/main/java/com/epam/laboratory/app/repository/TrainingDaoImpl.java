package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingDaoImpl extends AbstractDao<Training> implements TrainingDao {

    public TrainingDaoImpl(@Autowired Storage storage) {
        super(storage);
    }

}
