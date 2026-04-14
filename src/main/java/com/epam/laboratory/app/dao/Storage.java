package com.epam.laboratory.app.dao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Setter
@Getter
@PropertySource("classpath:application.properties")
public class Storage implements InitializingBean, DisposableBean {
    @Value("${storage.path}")
    private String storageFilePath;

    private final Map<String, Object> storage = new HashMap<>();

    public void put(String key, Object value) {
        storage.put(key, value);
    }

    public Object get(String key) {
        return storage.get(key);
    }

    public void remove(String key) {
        storage.remove(key);
    }

    public  void clear() {
        storage.clear();
    }

    public int size() {
        return storage.size();
    }

    public Set<String> keySet() {
        return storage.keySet();
    }

    public Collection<Object> values() {
        return storage.values();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Storage initialized with file path: " + storageFilePath);
    }

    @Override
    public void destroy() throws Exception {

    }
}
