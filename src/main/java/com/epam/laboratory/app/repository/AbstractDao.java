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

    protected Collection<T> findAll(Class<T> clazz) {
        return storage.values()
                .stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();
    }

    @Logging(Level.INFO)
    @Override
    public Collection<T> findByCondition(Predicate<T> condition, Class<T> clazz) {
        return storage.values()
                .stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .filter(condition)
                .toList();
    }

    @Logging(Level.INFO)
    @Override
    public T save(T entity) {
        storage.store(entity);

        return entity;
    }

    @Logging(Level.INFO)
    @Override
    public T update(T entity) {
        storage.update(entity);

        return entity;
    }

    @Logging(Level.INFO)
    @Override
    public void  delete(T entity) {
        storage.remove(entity);
    }
}
