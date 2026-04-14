package com.epam.laboratory.app.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public abstract class BaseDaoImpl<T, K> implements BaseDao<T, K> {
    protected final Storage storage;

    @SuppressWarnings("unchecked")
    protected Optional<T> findById(K id, Class<T> clazz) {
        String key = getKey(id, clazz);
        return Optional.ofNullable((T) storage.get(key));
    }

    protected Collection<T> findAll(Class<T> clazz) {
        return storage.values()
                .stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    private List<String> getKeysByPrefix(String keyPrefix) {
        return storage.keySet().stream()
                .filter(key -> key.startsWith(keyPrefix))
                .collect(Collectors.toList());
    }

    private String getKey(K id, Class<T> clazz) {
        return clazz.getSimpleName().toLowerCase() + ":" + id;
    }
}
