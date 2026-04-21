package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Entity;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Component
@RequiredArgsConstructor
@Setter
@Getter
@PropertySource("classpath:application.properties")
public class Storage implements InitializingBean, DisposableBean {
    private final JsonMapper jsonMapper;

    private final Map<String, Entity> storageMap = new HashMap<>();

    @Value("${storage.path}")
    private String storageFilePath;

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

    @Override
    public void afterPropertiesSet() throws Exception {
        var storageFile = getStorageFilePath();
        if (Files.exists(storageFile)) {
            String storedJson = Files.readString(storageFile);
            Map<String, LinkedHashMap<String, Object>> storedData = jsonMapper.readValue(storedJson, new TypeReference<>() {});

            for (Map.Entry<String, LinkedHashMap<String, Object>> entry : storedData.entrySet()) {
                String key = entry.getKey();
                LinkedHashMap<String, Object> value = entry.getValue();

                Entity typedValue = convertToConcreteType(key, value);
                storageMap.put(key, typedValue);
            }
        }
    }

    private Entity convertToConcreteType(String key, LinkedHashMap<String, Object> map) {
        String prefix = key.split(":")[0];

        return (Entity) switch (prefix) {
            case "trainee" -> jsonMapper.convertValue(map, Trainee.class);
            case "trainer" -> jsonMapper.convertValue(map, Trainer.class);
            case "training" -> jsonMapper.convertValue(map, Training.class);
            default -> map;
        };
    }

    @Override
    public void destroy() throws Exception {
        var storageFile = getStorageFilePath();
        String json = jsonMapper.writeValueAsString(storageMap);
        Files.writeString(storageFile, json, CREATE, TRUNCATE_EXISTING);
    }

    private Path getStorageFilePath() {
        return Path.of(storageFilePath);
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
