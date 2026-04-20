package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Entity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public abstract class AbstractDao<T extends Entity> implements BaseDao<T, Long> {
    protected final Storage storage;

    @SuppressWarnings("unchecked")
    protected Optional<T> findById(Long id, Class<T> clazz) {
        String key = getKey(id, clazz);
        return Optional.ofNullable((T) storage.get(key));
    }

    protected Collection<T> findAll(Class<T> clazz) {
        return storage.values()
                .stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();
    }

    protected Long save(T entity, Class<T> clazz) {
        String keyPrefix = getKeyPrefix(clazz);
        long id = computeNextId(keyPrefix);
        String key = getKey(id, clazz);
        storage.put(key, entity);

        return id;
    }

    protected T update(T entity, Long id, Class<T> clazz) {
        String key = getKey(id, clazz);
        storage.put(key, entity);

        return entity;
    }

    protected void  delete(Long id, Class<T> clazz) {
        String key = getKey(id, clazz);
        storage.remove(key);
    }

    private List<String> getKeysByPrefix(String keyPrefix) {
        return storage.keySet().stream()
                .filter(key -> key.startsWith(keyPrefix))
                .toList();
    }

    private long computeNextId(String keyPrefix) {
        List<String> keys = getKeysByPrefix(keyPrefix);
        return keys.stream()
                .map(key -> key.substring(keyPrefix.length() + 1))
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0L) + 1;
    }

    private String getKeyPrefix(Class<T> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    private String getKey(long id, Class<T> clazz) {
        return getKeyPrefix(clazz) + ":" + id;
    }
}
