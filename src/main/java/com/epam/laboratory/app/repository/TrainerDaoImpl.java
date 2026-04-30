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
public class TrainerDaoImpl extends AbstractUserDao<Trainer> implements TrainerDao {
    private static final String SELECT_BY_USERNAME_QUERY = "SELECT t FROM Trainer t WHERE t.username = :username";
    private static final String CHANGE_PASSWORD_QUERY = "UPDATE Trainer t SET t.password = :newPassword WHERE t.id = :id";
    private static final String CHANGE_STATUS_QUERY = "UPDATE Trainer t SET t.active = :isActive WHERE t.id = :id";
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
    public void changePassword(Long id, String newPassword) {
        var query = em.createQuery(CHANGE_PASSWORD_QUERY);
        query.setParameter("id", id);
        query.setParameter("newPassword", newPassword);
        query.executeUpdate();
    }

    @Logging(INFO)
    @Override
    public void changeStatus(Long id, boolean isActive) {
        var query = em.createQuery(CHANGE_STATUS_QUERY, Trainer.class);
        query.setParameter("isActive", isActive);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Logging(INFO)
    @Override
    public void deleteByUsername(String username) {
        var query = em.createQuery(DELETE_BY_USERNAME_QUERY, Trainer.class);
        query.setParameter("username", username);
        query.executeUpdate();
    }
}
