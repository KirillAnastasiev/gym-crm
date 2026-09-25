package com.kirill.projects.gymcrm.app.util;

import com.kirill.projects.gymcrm.app.domain.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsernameHelper {

    public static <T extends User> String generateUsername(T user, Predicate<String> isUsernameAlreadyExists) {
        String username = fetchUsername(user.getFirstName(), user.getLastName());
        while (isUsernameAlreadyExists.test(username)) {
            username = fetchUsernameWithSuffix(user.getFirstName(), user.getLastName(), String.valueOf(ThreadLocalRandom.current().nextInt(10000)));
        }
        return username;
    }

    static String fetchUsername(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

    static String fetchUsernameWithSuffix(String firstName, String lastName, String suffix) {
        return firstName + "." + lastName + suffix;
    }
}
