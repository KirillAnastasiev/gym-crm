package com.kirill.projects.gymcrm.app.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class InputDataValidator {
    private static final String ERROR_MESSAGE_TEMPLATE_NULL_VALUE = "%s must not be null";
    private static final String ERROR_MESSAGE_TEMPLATE_BLANK_VALUE = "%s must not be blank";

    public static void validateNotNull(Object value, String inputName) {
        if (value == null) {
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE_TEMPLATE_NULL_VALUE, inputName));
        }
    }

    public static void validateNotBlank(String value, String inputName) {
        if (value == null) {
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE_TEMPLATE_NULL_VALUE, inputName));
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE_TEMPLATE_BLANK_VALUE, inputName));
        }
    }

}
