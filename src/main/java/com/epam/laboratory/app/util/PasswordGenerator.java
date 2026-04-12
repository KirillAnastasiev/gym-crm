package com.epam.laboratory.app.util;

import java.util.concurrent.ThreadLocalRandom;

public class PasswordGenerator {
    private static final int PASSWORD_LENGTH = 10;

    public static String generatePassword() {
        return ThreadLocalRandom.current()
                .ints(PASSWORD_LENGTH, '!', '~' + 1)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
