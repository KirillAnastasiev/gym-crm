package com.kirill.projects.gymcrm.cucumber.util;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@NoArgsConstructor(access = PRIVATE)
public class BearerAuthenticationHeaderResolver {

    public static String resolve(String accessToken) {
        return "Bearer " + accessToken;
    }

}
