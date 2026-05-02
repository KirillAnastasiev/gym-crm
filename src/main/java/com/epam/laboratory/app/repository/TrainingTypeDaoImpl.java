package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.TrainingType;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingTypeDaoImpl extends AbstractDao<TrainingType> implements TrainingTypeDao {
    @Override
    public TrainingType save(TrainingType entity) {
        throw new UnsupportedOperationException("Not supported operation.");
    }

    @Override
    public TrainingType update(TrainingType entity) {
        throw  new UnsupportedOperationException("Not supported operation.");
    }

    @Override
    public void delete(TrainingType entity) {
        throw  new UnsupportedOperationException("Not supported operation.");
    }
}
