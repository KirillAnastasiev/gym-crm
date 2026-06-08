package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.User;
import org.slf4j.event.Level;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserDao<T extends User> extends EntityDao<T> {

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    Optional<T> findByUsername(String username);

    @Logging(Level.INFO)
    void deleteByUsername(String username);

}
