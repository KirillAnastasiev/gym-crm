package com.epam.laboratory.app.util;


import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.toMap;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SensitiveDataMasker {

    private final ObjectMapper objectMapper;

    public Object maskSensitiveData(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            if (isPrimitiveOrWrapper(obj.getClass()) || isString(obj.getClass())) {
                return obj;
            } else if (obj.getClass().isArray()) {
                return markSensitiveDataInArray((Object[]) obj);
            } else if (obj instanceof Collection<?> collection) {
                return markSensitiveDataInCollection(collection);
            } else if (obj instanceof Map<?, ?> map) {
                return maskSensitiveDataInMap(map);
            } else {
                return maskSensitiveFieldsInObject(obj);
            }
        } catch (Exception e) {
            var logMessage = "Failed to mask sensitive data for object of type %s: %s".formatted(getClassName(obj), e.getMessage());
            log.warn(logMessage);
            return obj;
        }
    }

    private Object maskSensitiveFieldsInObject(Object obj) throws IllegalAccessException {
        ObjectNode jsonNode = objectMapper.valueToTree(obj);

        for (var field : obj.getClass().getDeclaredFields()) {
            if (field.getType().isArray()) {
                maskSensitiveField(obj, field, jsonNode, (fieldValue, node) -> {
                    var arrayValue = (Object[]) fieldValue;
                    node.putPOJO(field.getName(), markSensitiveDataInArray(arrayValue));
                });
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                maskSensitiveField(obj, field, jsonNode, (fieldValue, node) -> {
                    var collection = (Collection<?>) fieldValue;
                    node.putPOJO(field.getName(), markSensitiveDataInCollection(collection));
                });
            } else if (Map.class.isAssignableFrom(field.getType())) {
                maskSensitiveField(obj, field, jsonNode, (fieldValue, node) -> {
                    var map = (Map<?, ?>) fieldValue;
                    node.putPOJO(field.getName(), maskSensitiveDataInMap(map));
                });
            } else if (field.isAnnotationPresent(Sensitive.class)) {
                maskSensitiveField(obj, field, jsonNode, (fieldValue, node) -> {
                    String maskedValue = maskSensitiveString(fieldValue.toString());
                    node.put(field.getName(), maskedValue);
                });
            }
        }
        return jsonNode;
    }
    private String maskSensitiveString(String str) {
        if  (str == null) {
            return null;
        }
        return "*".repeat(str.length());
    }

    private void maskSensitiveField(Object obj,
                                    Field field,
                                    ObjectNode jsonNode,
                                    BiConsumer<Object, ObjectNode> sensitiveFieldMarker) throws IllegalAccessException {
        try {
            field.setAccessible(true);
            Object fieldValue = field.get(obj);
            if (fieldValue != null) {
                sensitiveFieldMarker.accept(fieldValue, jsonNode);
            }
        } finally {
            field.setAccessible(false);
        }
    }

    private Object[] markSensitiveDataInArray(Object[] array) {
        try {
            for (int i = 0; i < array.length; i++) {
                var jsonNode = maskSensitiveData(array[i]);
                array[i] = objectMapper.readValue(jsonNode.toString(), array[i].getClass());
            }
        } catch (JsonProcessingException e) {
            var logMessage = "Failed to mask sensitive data in array of type %s: %s".formatted(getClassName(array), e.getMessage());
            log.warn(logMessage);
         }
        return array;
    }

    private List<Object> markSensitiveDataInCollection(Collection<?> collection) {
        return collection.stream()
                .map(this::maskSensitiveData)
                .toList();
    }

    private Map<?, Object> maskSensitiveDataInMap(Map<?, ?> map) {
        return map.entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> maskSensitiveData(entry.getValue())));
    }

    private static String getClassName(Object obj) {
        return obj.getClass().getName();
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == Boolean.class ||
                type == Byte.class ||
                type == Character.class ||
                type == Short.class ||
                type == Integer.class ||
                type == Long.class ||
                type == Float.class ||
                type == Double.class;
    }

    private static boolean isString(Class<?> type) {
        return type == String.class;
    }

}
