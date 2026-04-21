package com.epam.laboratory.app.util;

import com.epam.laboratory.app.domain.User;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class UsernameHelper {

    public String generateUsername(String firstName, String lastName, Collection<? extends User> usersWithSuchFirstNameAndLastName) {
        boolean isAlreadyExists = !usersWithSuchFirstNameAndLastName.isEmpty();
        if (isAlreadyExists) {
            long traineesCount = usersWithSuchFirstNameAndLastName.size();
            return generateUsername(firstName, lastName, String.valueOf(traineesCount + 1));
        } else {
            return generateUsername(firstName, lastName);
        }
    }

    public static String generateUsername(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

    public static String generateUsername(String firstName, String lastName, String suffix) {
        return generateUsername(firstName, lastName) + suffix;
    }
}
