package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Entity;
import com.epam.laboratory.app.repository.Dao;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;

import java.util.Collection;
import java.util.function.Predicate;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public abstract class AbstractService<T extends Entity> implements Service<T> {
    protected final Dao<T> dao;

    @Logging(Level.INFO)
    @Override
    public T create(T entity) {
        prepareEntity(entity);
        return dao.save(entity);
    }

    @Logging(Level.INFO)
    @Override
    public T update(T entity) {
        prepareEntity(entity);
        return dao.update(entity);
    }

    @Logging(Level.INFO)
    @Override
    public void delete(T entity) {
        dao.delete(entity);
    }

    @Logging(Level.INFO)
    @Override
    public Collection<T> selectByCondition(Predicate<T> condition, Class<T> entityClass) {
        return dao.findByCondition(condition, entityClass);
    }

    protected abstract void prepareEntity(T entity);
}
