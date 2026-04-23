package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Entity;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
@RequiredArgsConstructor
public abstract class AbstractDao<T extends Entity> implements Dao<T> {
    protected final Storage storage;

    @SuppressWarnings("unchecked")
    @Logging(Level.INFO)
    @Override
    public Optional<T> findById(Long id, Class<T> clazz) {
        return Optional.ofNullable((T) storage.retrieveById(id, clazz));
    }

    @Logging(Level.INFO)
    @Override
    public Collection<T> findByCondition(Predicate<T> condition, Class<T> clazz) {
        return storage.retrieveByCondition(condition, clazz);
    }

    @SuppressWarnings("unchecked")
    @Logging(Level.INFO)
    @Override
    public T save(T entity) {
        return (T) storage.store(entity);
    }

    @SuppressWarnings("unchecked")
    @Logging(Level.INFO)
    @Override
    public T update(T entity) {
        return (T) storage.update(entity);
    }

    @Logging(Level.INFO)
    @Override
    public void  delete(T entity) {
        storage.remove(entity);
    }
}
