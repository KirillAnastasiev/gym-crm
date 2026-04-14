package com.epam.laboratory.app.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public abstract class BaseDaoImpl<T, K> implements BaseDao<T, K> {
    protected final Storage storage;

    @SuppressWarnings("unchecked")
    protected Optional<T> findById(K id, Class<T> clazz) {
        String key = getKey(id, clazz);
        return Optional.ofNullable((T) storage.get(key));
    }

    private String getKey(K id, Class<T> clazz) {
        return clazz.getSimpleName().toLowerCase() + ":" + id;
    }
}
