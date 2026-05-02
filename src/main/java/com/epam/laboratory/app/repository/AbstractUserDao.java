package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static org.slf4j.event.Level.INFO;

@Repository
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public abstract class AbstractUserDao<T extends User> extends AbstractDao<T> implements UserDao<T> {
    private static final String EXISTS_BY_USERNAME_QUERY = "SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.username = :username";

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public boolean existsByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        if (username.isBlank()) {
            return false;
        }
        var query = em.createQuery(EXISTS_BY_USERNAME_QUERY, Boolean.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }
}
