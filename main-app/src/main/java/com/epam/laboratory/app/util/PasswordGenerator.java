package com.epam.laboratory.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordGenerator {
    private static final int PASSWORD_LENGTH = 10;

    public static String generatePassword() {
        return ThreadLocalRandom.current()
                .ints(PASSWORD_LENGTH, '!', '~' + 1)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
