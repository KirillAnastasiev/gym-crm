package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainer;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.slf4j.event.Level.INFO;

@Repository
@Transactional(rollbackFor = Exception.class)
public class TrainerDaoImpl extends AbstractEntityDao<Trainer> implements TrainerDao {
    private static final String SELECT_BY_USERNAME_QUERY = "SELECT t FROM Trainer t WHERE t.username = :username";
    private static final String UPDATE_BY_USERNAME_QUERY = "UPDATE Trainer t SET t.firstName = :firstName, t.lastName = :lastName, t.specialization = :specialization, t.active = :isActive WHERE t.username = :username";
    private static final String CHANGE_STATUS_BY_USERNAME_QUERY = "UPDATE Trainer t SET t.active = :isActive WHERE t.username = :username";
    private static final String DELETE_BY_USERNAME_QUERY = "DELETE FROM Trainer t WHERE t.username = :username";

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Optional<Trainer> findByUsername(String username) {
        try {
            var query = em.createQuery(SELECT_BY_USERNAME_QUERY, Trainer.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Logging(INFO)
    @Override
    public Trainer update(Trainer entity) {
        var query = em.createQuery(UPDATE_BY_USERNAME_QUERY);
        query.setParameter("firstName", entity.getFirstName());
        query.setParameter("lastName", entity.getLastName());
        query.setParameter("specialization", entity.getSpecialization());
        query.setParameter("isActive", entity.getActive());
        query.setParameter("username", entity.getUsername());
        query.executeUpdate();
        return findByUsername(entity.getUsername()).get();
    }

    @Logging(INFO)
    @Override
    public void changeStatusByUsername(String username, boolean isActive) {
        var query = em.createQuery(CHANGE_STATUS_BY_USERNAME_QUERY);
        query.setParameter("isActive", isActive);
        query.setParameter("username", username);
        query.executeUpdate();
    }

    @Logging(INFO)
    @Override
    public void deleteByUsername(String username) {
        var query = em.createQuery(DELETE_BY_USERNAME_QUERY);
        query.setParameter("username", username);
        query.executeUpdate();
    }
}
