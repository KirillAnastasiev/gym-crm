package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Entity;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Setter
@Getter
@PropertySource("classpath:application.properties")
public class Storage {
    private final JsonMapper jsonMapper;

    private final Map<String, Entity> storageMap = new HashMap<>();

    public void store(Entity entity) {
        var entityClass = getEntityClass(entity);
        var keyPrefix = getKeyPrefix(entityClass);
        var id = computeNextId(keyPrefix);
        entity.setId(id);
        var key = getKey(id, entityClass);
        storageMap.put(key, entity);
    }

    public void update(Entity entity) {
        var entityClass = getEntityClass(entity);
        var key = getKey(entity.getId(), entityClass);
        storageMap.put(key, entity);
    }

    public Entity retrieveById(long id, Class<? extends Entity> clazz) {
        var key = getKey(id, clazz);
        return storageMap.get(key);
    }

    public void remove(Entity entity) {
        var entityClass = getEntityClass(entity);
        var key = getKey(entity.getId(), entityClass);
        storageMap.remove(key);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> Collection<T> retrieveByCondition(Predicate<T> condition, Class<T> clazz) {
        return storageMap.values()
                .stream()
                .filter(clazz::isInstance)
                .map(e -> (T) e)
                .filter(condition)
                .toList();
    }

    private List<String> getKeysByPrefix(String keyPrefix) {
        return storageMap.keySet().stream()
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

    private String getKeyPrefix(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    private String getKey(long id, Class<?> clazz) {
        return getKeyPrefix(clazz) + ":" + id;
    }

    private Class<? extends Entity> getEntityClass(Entity entity) {
        return entity.getClass();
    }
}
