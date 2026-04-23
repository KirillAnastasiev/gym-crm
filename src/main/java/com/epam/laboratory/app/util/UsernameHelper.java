package com.epam.laboratory.app.util;

import com.epam.laboratory.app.domain.User;
import com.epam.laboratory.app.repository.Dao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class UsernameHelper {

    public <T extends User> boolean testUsernameAlreadyExists(String username, Dao<T> dao, Class<T> clazz) {
        return !dao.findByCondition(u -> u.getUsername().equals(username), clazz).isEmpty();
    }

    public <T extends User> String generateUsername(T user, Dao<T> dao) {
        String username = generateUsername(user.getFirstName(), user.getLastName());
        while (testUsernameAlreadyExists(username, dao, (Class<T>) user.getClass())) {
            username = generateUsername(user.getFirstName(), user.getLastName(), String.valueOf(ThreadLocalRandom.current().nextInt(10000)));
        }
        return username;
    }

    public static String generateUsername(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

    public static String generateUsername(String firstName, String lastName, String suffix) {
        return generateUsername(firstName, lastName) + suffix;
    }
}
