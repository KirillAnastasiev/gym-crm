package com.epam.laboratory.app.config;

import com.epam.laboratory.app.domain.Entity;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.repository.Storage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.stream.Collectors.*;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class CustomStorageBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    private final JsonMapper jsonMapper;

    @Value("${storage.path}")
    private String storageFilePath;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof Storage storage)) {
            return bean;
        }

        Path storageFile = Path.of(storageFilePath);
        if (!Files.exists(storageFile)) {
            return bean;
        }

        try {
            String storedJson = Files.readString(storageFile);

            Map<String, LinkedHashMap<String, Object>> storedData =
                    jsonMapper.readValue(storedJson, new TypeReference<>() {});

            for (Map.Entry<String, LinkedHashMap<String, Object>> entry : storedData.entrySet()) {
                String key = entry.getKey();
                LinkedHashMap<String, Object> value = entry.getValue();
                Entity typedValue = convertToConcreteType(key, value);
                storage.getStorageMap().put(key, typedValue);
            }

            var classToMaxId = storage.getStorageMap()
                    .values()
                    .stream()
                    .collect(groupingBy(Entity::getClass, collectingAndThen(mapping(Entity::getId, maxBy(Long::compare)), optional -> optional.orElse(0L))));

            storage.getEntityKeyMapper()
                    .getClassToLastUsedId()
                    .putAll(classToMaxId);

        } catch (Exception e) {
            throw new BeanCreationException("Failed to initialize Storage from file", e);
        }

        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof Storage storage)) {
            return;
        }

        try {
            Path storageFile = Path.of(storageFilePath);
            String json = jsonMapper.writeValueAsString(storage.getStorageMap());
            Files.writeString(storageFile, json, CREATE, TRUNCATE_EXISTING);
        } catch (Exception e) {
            throw new BeansException("Failed to persist Storage to file", e) {};
        }
    }

    private Entity convertToConcreteType(String key, LinkedHashMap<String, Object> map) {
        String prefix = key.split(":")[0];

        return switch (prefix) {
            case "trainee" -> jsonMapper.convertValue(map, Trainee.class);
            case "trainer" -> jsonMapper.convertValue(map, Trainer.class);
            case "training" -> jsonMapper.convertValue(map, Training.class);
            default -> throw new IllegalArgumentException("Unknown storage key prefix: " + prefix);
        };
    }
}

