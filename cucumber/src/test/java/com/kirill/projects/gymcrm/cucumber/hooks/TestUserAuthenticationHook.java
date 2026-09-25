package com.kirill.projects.gymcrm.cucumber.hooks;

import com.kirill.projects.gymcrm.cucumber.util.TestUserAuthenticator;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestUserAuthenticationHook {

    private final TestUserAuthenticator authenticator;

    @Before
    public void beforeCallingScenario() {
        authenticator.authenticate();
    }
}
