package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.TrainingType;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class TrainingTypeDaoImpl extends AbstractEntityDao<TrainingType> implements TrainingTypeDao {
    private static final String FIND_ALL_QUERY = "SELECT t FROM TrainingType t";
    private static final String FIND_BY_NAME_QUERY = "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :trainingTypeName";

    @Override
    public TrainingType save(TrainingType entity) {
        throw new UnsupportedOperationException("Not supported operation.");
    }

    @Override
    public TrainingType update(TrainingType entity) {
        throw new UnsupportedOperationException("Not supported operation.");
    }

    @Override
    public Collection<TrainingType> findAll() {
        var query = em.createQuery(FIND_ALL_QUERY, TrainingType.class);
        return query.getResultList();
    }

    @Override
    public Optional<TrainingType> findByTrainingTypeName(String trainingTypeName) {
        try {
            var query = em.createQuery(FIND_BY_NAME_QUERY, TrainingType.class);
            query.setParameter("trainingTypeName", trainingTypeName);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
