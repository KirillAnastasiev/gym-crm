package com.epam.laboratory.app.dao;

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
import java.util.Collection;
import java.util.HashMap;
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

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
        var storageFile = getStorageFilePath();
        if (Files.exists(storageFile)) {
            String storedJson = Files.readString(storageFile);
            var storedData = jsonMapper.readValue(storedJson, Map.class);
            storageMap.putAll(storedData);
        }
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
