package com.epam.laboratory.app.util;

import com.epam.laboratory.app.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UsernameHelper {
    public String generateUsername(User user) {
        var firstName = user.getFirstName();
        var lastName = user.getLastName();

        return firstName + "." + lastName;
    }

    public String generateUsername(User user, String suffix) {
        return generateUsername(user) + suffix;
    }
}
