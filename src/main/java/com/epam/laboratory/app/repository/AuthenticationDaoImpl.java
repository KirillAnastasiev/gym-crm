package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.slf4j.event.Level.INFO;

@Repository
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AuthenticationDaoImpl implements AuthenticationDao {
    private static final String EXISTS_BY_USERNAME_QUERY = "SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.username = :username";
    private static final String CHANGE_PASSWORD_FOR_USERNAME_QUERY = "UPDATE User u SET u.password = :newPassword WHERE u.username = :username";
    private static final String FIND_BY_USERNAME_QUERY = "SELECT u FROM User u WHERE u.username = :username";

    @PersistenceContext
    private EntityManager em;

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public boolean checkExistsByUsername(String username) {
        var query = em.createQuery(EXISTS_BY_USERNAME_QUERY, Boolean.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    @Logging(INFO)
    @Override
    public void changePasswordForUsername(String username, String newPassword) {
        var query = em.createQuery(CHANGE_PASSWORD_FOR_USERNAME_QUERY);
        query.setParameter("username", username);
        query.setParameter("newPassword", newPassword);
        query.executeUpdate();
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Optional<User> findUserByUsername(String username) {
        try {
            var query = em.createQuery(FIND_BY_USERNAME_QUERY, User.class);
            query.setParameter("username", username);
            return Optional.ofNullable(query.getSingleResult());
        } catch (PersistenceException e) {
            return Optional.empty();
        }
    }

}
