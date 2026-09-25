package com.kirill.projects.gymcrm.cucumber.util;

import com.kirill.projects.gymcrm.cucumber.dto.Credentials;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicAuthenticationHeaderResolver {

    public static String resolve(Credentials credentials) {
        String authString = credentials.username() + ":" + credentials.password();
        return "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
    }

}
