package com.epam.laboratory.app.util;

import com.epam.laboratory.app.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UsernameHelper {
    public String generateUsername(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

    public String generateUsername(String firstName, String lastName, String suffix) {
        return generateUsername(firstName, lastName) + suffix;
    }
}
