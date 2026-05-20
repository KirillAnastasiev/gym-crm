package com.epam.laboratory.app.util;

import com.epam.laboratory.app.dto.annotation.Required;
import com.epam.laboratory.app.exception.DtoValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DtoValidator {

    public static void validate(Object dto) {
        try {
            if (dto == null) {
                throw new DtoValidationException("DTO must not be null");
            }
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
