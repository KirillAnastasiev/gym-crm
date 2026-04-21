package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Training;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TrainingDaoImpl extends AbstractDao<Training> implements TrainingDao {

    public TrainingDaoImpl(@Autowired Storage storage) {
        super(storage);
    }

}
