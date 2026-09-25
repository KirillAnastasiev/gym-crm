package com.kirill.projects.gymcrm.app.service;

import com.kirill.projects.gymcrm.app.aspect.annotation.Logging;
import com.kirill.projects.gymcrm.app.domain.TrainingType;
import com.kirill.projects.gymcrm.app.exception.NoSuchEntityException;
import com.kirill.projects.gymcrm.app.repository.TrainingTypeDao;
import com.kirill.projects.gymcrm.app.util.InputDataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class TrainingTypeServiceImpl extends AbstractEntityService<TrainingType> implements TrainingTypeService {

    @Autowired
    public TrainingTypeServiceImpl(TrainingTypeDao dao) {
        super(dao);
    }

    @Override
    public TrainingType update(TrainingType entity) {
        throw new UnsupportedOperationException("Unavailable operation for training type");
    }

    @Logging(INFO)
    @Override
    public Collection<TrainingType> selectAll() {
        return ((TrainingTypeDao) dao).findAll();
    }

    @Logging(INFO)
    @Override
    public TrainingType selectByTrainingTypeName(String trainingTypeName) {
        InputDataValidator.validateNotBlank(trainingTypeName, "Training type name");
        var optionalTrainingType = ((TrainingTypeDao) dao).findByTrainingTypeName(trainingTypeName);
        return optionalTrainingType.orElseThrow(() ->
                new NoSuchEntityException("Training type with name " + trainingTypeName + " not found"));
    }

}
