package com.epam.laboratory.cucumber.config;

import org.springframework.test.context.ActiveProfilesResolver;

public class EnvironmentResolver implements ActiveProfilesResolver {
    private static final String ACTIVE_PROFILE_KEY = "spring.profiles.active";
    private static final String DEFAULT_PROFILE = "dev";

    @Override
    public String[] resolve(Class<?> aClass) {
        String activeProfile = System.getProperty(ACTIVE_PROFILE_KEY);
        if (activeProfile == null || activeProfile.isEmpty()) {
            activeProfile = DEFAULT_PROFILE;
        }
        return new String[] { activeProfile };
    }
}
