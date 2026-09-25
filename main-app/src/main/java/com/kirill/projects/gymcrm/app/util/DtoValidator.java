package com.kirill.projects.gymcrm.app.util;

import com.kirill.projects.gymcrm.app.dto.annotation.Required;
import com.kirill.projects.gymcrm.app.exception.DtoValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DtoValidator {

    public static void validate(Object dto) {
        try {
            InputDataValidator.validateNotNull(dto, "DTO");
            for (var field : dto.getClass().getRecordComponents()) {
                if (field.isAnnotationPresent(Required.class)) {
                    var value = field.getAccessor().invoke(dto);
                    if (value == null) {
                        throw new DtoValidationException("Property '" + field.getName() + "' is required");
                    }
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new DtoValidationException(e.getMessage());
        }
    }

}
