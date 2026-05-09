package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainee;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.slf4j.event.Level.INFO;

@Repository
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TraineeDaoImpl extends AbstractEntityDao<Trainee> implements TraineeDao {
    private static final String SELECT_BY_USERNAME_QUERY = "SELECT t FROM Trainee t WHERE t.username = :username";
    private static final String UPDATE_BY_USERNAME_QUERY = "UPDATE Trainee t SET t.firstName = :firstName, t.lastName = :lastName, t.dateOfBirth = :dateOfBirth, t.address = :address, t.active = :isActive WHERE t.username = :username";
    private static final String CHANGE_STATUS_BY_USERNAME_QUERY = "UPDATE Trainee t SET t.active = :isActive WHERE t.username = :username";
    private static final String DELETE_BY_USERNAME_QUERY = "DELETE FROM Trainee t WHERE t.username = :username";

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Optional<Trainee> findByUsername(String username) {
        try {
            var query = em.createQuery(SELECT_BY_USERNAME_QUERY, Trainee.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Logging(INFO)
    @Override
    public Trainee update(Trainee entity) {
        var query = em.createQuery(UPDATE_BY_USERNAME_QUERY);
        query.setParameter("firstName", entity.getFirstName());
        query.setParameter("lastName", entity.getLastName());
        query.setParameter("dateOfBirth", entity.getDateOfBirth());
        query.setParameter("address", entity.getAddress());
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