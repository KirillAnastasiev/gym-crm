package com.epam.laboratory.cucumber.steps;

import com.epam.laboratory.cucumber.client.ApiClient;
import com.epam.laboratory.cucumber.util.BearerAuthenticationHeaderResolver;
import com.epam.laboratory.cucumber.util.ContextHolder;
import com.epam.laboratory.cucumber.util.ContextHolder.Key;
import com.epam.laboratory.cucumber.util.TestUserAuthenticator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LogoutTestFeatureSteps {
    private static final Key<String> ACCES_TOKEN_KEY = Key.of("accessToken", String.class);
    private static final Key<Integer> STATUS_CODE_KEY = Key.of("statusCode", Integer.class);
    private static final Key<String> RESPONSE_BODY_KEY = Key.of("logoutResponseBody", String.class);

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;


    // ==================== LOGOUT TEST STEPS SUCCESSFUL ====================

    @Given("The user is authenticated with authenticates with valid JWT token")
    public void the_user_is_authenticated_with_authenticates_with_valid_jwt_token() {
        var tokens = contextHolder.get(TestUserAuthenticator.TOKENS_KEY);
        assertThat(tokens).isNotNull();
        var accessToken = tokens.accessToken();
        assertThat(accessToken).isNotNull();
        contextHolder.put(ACCES_TOKEN_KEY, accessToken);
    }

    @When("The user attempts to log out sending a {string} request to the logout endpoint {string}")
    public void the_user_attempts_to_log_out_sending_request_to_the_logout_endpoint(String method, String endpoint) throws IOException, InterruptedException {
        var accessToken = contextHolder.get(ACCES_TOKEN_KEY);
        try (var client = apiClient.spec()) {
            var authHeader = BearerAuthenticationHeaderResolver.resolve(accessToken);
            var request = apiClient.request(endpoint, method, Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader), null);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
            contextHolder.put(RESPONSE_BODY_KEY, response.body());
        }
    }

}
