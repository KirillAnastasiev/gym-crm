package com.epam.laboratory.cucumber.hooks;

import com.epam.laboratory.cucumber.util.TestUserAuthenticator;
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
