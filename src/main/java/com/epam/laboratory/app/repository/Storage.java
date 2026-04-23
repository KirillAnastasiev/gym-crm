package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Entity;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Setter
@Getter
public class Storage {
    private final JsonMapper jsonMapper;

    private final EntityKeyMapper entityKeyMapper;

    private final Map<String, Entity> storageMap = new HashMap<>();

    public Entity store(Entity entity) {
        var id = computeNextId(entity.getClass());
        entity.setId(id);
        var key = entityKeyMapper.getKey(id, entity.getClass());
        storageMap.put(key, entity);
        return entity;
    }

    public Entity update(Entity entity) {
        var key = entityKeyMapper.getKey(entity.getId(), entity.getClass());
        storageMap.put(key, entity);
        return entity;
    }

    public Entity retrieveById(long id, Class<? extends Entity> clazz) {
        var key = entityKeyMapper.getKey(id, clazz);
        return storageMap.get(key);
    }

    public <T extends Entity> Collection<T> retrieveByCondition(Predicate<? super T> condition, Class<T> clazz) {
        return storageMap.values()
                .stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .filter(condition)
                .toList();
    }

    public void remove(Entity entity) {
        var key = entityKeyMapper.getKey(entity.getId(), entity.getClass());
        storageMap.remove(key);
    }

    private long computeNextId(Class<? extends Entity> clazz) {
        return entityKeyMapper.incrementAndGetLastUsedId(clazz);
    }

    @Component
    @Getter
    public static class EntityKeyMapper {
        private final Map<Class<? extends Entity>, Long> classToLastUsedId = new HashMap<>();

        public String getKeyPrefix(Class<? extends Entity> clazz) {
            return clazz.getSimpleName().toLowerCase();
        }

        public String getKey(long id, Class<? extends Entity> clazz) {
            return getKeyPrefix(clazz) + ":" + id;
        }

        public long incrementAndGetLastUsedId(Class<? extends Entity> clazz) {
            Long id = classToLastUsedId.get(clazz);
            if (id == null) {
                id = 0L;
            }
            id++;
            classToLastUsedId.put(clazz, id);
            return id;
        }
    }
}
