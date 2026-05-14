package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.TrainingTypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@org.springframework.stereotype.Service
@Transactional(rollbackFor = Exception.class)
public class TrainingTypeServiceImpl extends AbstractEntityService<TrainingType> implements TrainingTypeService {

    @Autowired
    public TrainingTypeServiceImpl(TrainingTypeDao dao) {
        super(dao);
    }

    @Override
    public Collection<TrainingType> selectAll() {
        return ((TrainingTypeDao) dao).findAll();
    }

    @Override
    public TrainingType selectByTrainingTypeName(String trainingTypeName) {
        if (trainingTypeName == null) {
            throw new IllegalArgumentException("Training type name must not be null");
        }
        if (trainingTypeName.isBlank()) {
            throw new IllegalArgumentException("Training type name must not be blank");
        }
        var optionalTrainingType = ((TrainingTypeDao) dao).findByTrainingTypeName(trainingTypeName);
        return optionalTrainingType.orElseThrow(() ->
                new NoSuchEntityException("Training type with name " + trainingTypeName + " not found"));
    }

    @Override
    protected void prepareEntity(TrainingType entity) {
        // do nothing
    }
}
