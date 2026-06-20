package com.epam.laboratory.cucumber.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ContextHolder {
    private final Map<Key<?>, Object> context = new HashMap<>();

    public <T> void put(Key<T> key, T value) {
        context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Key<T> key) {
        return (T) context.get(key);
    }

    public void clear() {
        context.clear();
    }

    @RequiredArgsConstructor
    @Data
    public static final class Key<T> {
        private final String name;
        private final Class<T> type;

        public static <T> Key<T> of(String name, Class<T> type) {
            return new Key<>(name, type);
        }
    }
}
