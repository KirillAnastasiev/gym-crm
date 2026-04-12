package com.epam.laboratory.app.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class PasswordGenerator {
    private static final int PASSWORD_LENGTH = 10;

    public String generatePassword() {
        return ThreadLocalRandom.current()
                .ints(PASSWORD_LENGTH, '!', '~' + 1)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
