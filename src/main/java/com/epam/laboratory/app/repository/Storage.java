package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Component
@RequiredArgsConstructor
@Setter
@Getter
@PropertySource("classpath:application.properties")
public class Storage implements InitializingBean, DisposableBean {
    private final JsonMapper jsonMapper;

    private final Map<String, Object> storageMap = new HashMap<>();

    @Value("${storage.path}")
    private String storageFilePath;

    public void put(String key, Object value) {
        storageMap.put(key, value);
    }

    public Object get(String key) {
        return storageMap.get(key);
    }

    public void remove(String key) {
        storageMap.remove(key);
    }

    public  void clear() {
        storageMap.clear();
    }

    public int size() {
        return storageMap.size();
    }

    public Set<String> keySet() {
        return storageMap.keySet();
    }

    public Collection<Object> values() {
        return storageMap.values();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        var storageFile = getStorageFilePath();
        if (Files.exists(storageFile)) {
            String storedJson = Files.readString(storageFile);
            Map<String, LinkedHashMap<String, ?>> storedData = jsonMapper.readValue(storedJson, new TypeReference<>() {});

            for (Map.Entry<String, LinkedHashMap<String, ?>> entry : storedData.entrySet()) {
                String key = entry.getKey();
                LinkedHashMap<String, ?> value = entry.getValue();

                Object typedValue = convertToConcreteType(key, value);
                storageMap.put(key, typedValue);
            }
        }
    }

    private Object convertToConcreteType(String key, LinkedHashMap<String, ?> map) {
        String prefix = key.split(":")[0];

        return switch (prefix) {
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
}
