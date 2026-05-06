package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.repository.TrainingTypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@Transactional(rollbackFor = Exception.class)
public class TrainingTypeServiceImpl extends AbstractEntityService<TrainingType> implements TrainingTypeService {

    public TrainingTypeServiceImpl(@Autowired TrainingTypeDao dao) {
        super(dao);
    }

    @Override
    protected void prepareEntity(TrainingType entity) {
        // do nothing
    }
}
