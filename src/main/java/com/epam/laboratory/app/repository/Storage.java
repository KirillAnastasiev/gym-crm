package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Entity;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
public class Storage {
    private final JsonMapper jsonMapper;

    private final EntityKeyMapper entityKeyMapper;

    private final Map<String, Entity> storageMap = new HashMap<>();

    public Entity store(Entity entity) {
        var keyPrefix = entityKeyMapper.getKeyPrefix(entity);
        var id = computeNextId(keyPrefix);
        var key = entityKeyMapper.getKey(id, entity);
        entity.setId(id);
        storageMap.put(key, entity);
        return entity;
    }

    public Entity update(Entity entity) {
        var key = entityKeyMapper.getKey(entity.getId(), entity);
        storageMap.put(key, entity);
        return entity;
    }

    public Entity retrieveById(long id, Class<? extends Entity> clazz) {
        var key = entityKeyMapper.getKey(id, clazz);
        return storageMap.get(key);
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

    public void remove(Entity entity) {
        var key = entityKeyMapper.getKey(entity.getId(), entity);
        storageMap.remove(key);
    }

    private long computeNextId(String keyPrefix) {
        return getKeysByPrefix(keyPrefix)
                .stream()
                .map(key -> key.substring(keyPrefix.length() + 1))
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0L) + 1;
    }

    private List<String> getKeysByPrefix(String keyPrefix) {
        return storageMap.keySet()
                .stream()
                .filter(key -> key.startsWith(keyPrefix))
                .toList();
    }

    @Component
    public static class EntityKeyMapper {
        private final Map<Class<? extends Entity>, String> classToKeyPrefixMap = Map.of(
                Trainee.class, "trainee",
                Trainer.class, "trainer",
                Training.class, "training"
        );

        public String getKey(long id, Entity entity) {
            return getKeyPrefix(entity) + ":" + id;
        }

        public String getKey(long id, Class<? extends Entity> clazz) {
            return classToKeyPrefixMap.get(clazz) + ":" + id;
        }

        public String getKeyPrefix(Entity entity) {
            Class<? extends Entity> clazz = entity.getClass();
            return classToKeyPrefixMap.get(clazz);
        }

        public String getKeyPrefix(Class<? extends Entity> clazz) {
            return classToKeyPrefixMap.get(clazz);
        }
    }
}
