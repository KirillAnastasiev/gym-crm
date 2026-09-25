package com.kirill.projects.gymcrm.cucumber.steps;

import com.kirill.projects.gymcrm.cucumber.client.ApiClient;
import com.kirill.projects.gymcrm.cucumber.dto.Credentials;
import com.kirill.projects.gymcrm.cucumber.dto.TokensResponse;
import com.kirill.projects.gymcrm.cucumber.util.BasicAuthenticationHeaderResolver;
import com.kirill.projects.gymcrm.cucumber.util.ContextHolder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.kirill.projects.gymcrm.cucumber.util.ContextHolder.Key;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AuthenticationTestFeatureSteps {
    private static final Key<Credentials> CREDENTIALS_KEY = Key.of("credentials", Credentials.class);
    private static final Key<Integer> STATUS_CODE_KEY = Key.of("statusCode", Integer.class);
    private static final Key<String> RESPONSE_BODY_KEY = Key.of("responseBody", String.class);

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JsonMapper jsonMapper;


    // ==================== AUTHENTICATION TEST STEPS SUCCESSFUL ====================

    @Given("The user has valid credentials with username {string} and password {string}")
    public void the_user_has_valid_credentials_with_username_and_password(String username, String password) {
        var credentials = new Credentials(username, password);
        contextHolder.put(CREDENTIALS_KEY, credentials);
    }

    @When("The user attempts to log in by sending a {string} request to the authentication endpoint {string} with the provided credentials")
    public void the_user_attempts_to_log_in_by_sending_a_get_request_to_the_authentication_endpoint_with_the_provided_credentials(String httpMethod, String endpoint) throws IOException, InterruptedException {
        try (var httpClient = apiClient.spec()) {
            var authHeader = BasicAuthenticationHeaderResolver.resolve(contextHolder.get(CREDENTIALS_KEY));
            var authRequest = apiClient.request(endpoint, httpMethod, Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader), null);
            var response = httpClient.send(authRequest, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
            contextHolder.put(RESPONSE_BODY_KEY, response.body());
        }
    }

    @Then("The the user receives a successful response with status code {int}")
    public void the_the_user_receives_a_successful_response_with_status_code(int expectedStatus) {
        var actualStatus = contextHolder.get(STATUS_CODE_KEY);
        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @Then("The user receives access and refresh JWT tokens")
    public void the_user_receives_access_and_refresh_jwt_tokens() {
        var responseBody = contextHolder.get(RESPONSE_BODY_KEY);
        var tokens = jsonMapper.readValue(responseBody, TokensResponse.class);
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
    }


    // ==================== AUTHENTICATION TEST STEPS FAILURE ====================

    @Given("The user has invalid credentials with username {string} and password {string}")
    public void the_user_has_invalid_credentials_with_username_and_password(String username, String password) {
        var credentials = new Credentials(username, password);
        contextHolder.put(CREDENTIALS_KEY, credentials);
    }

    @Then("The user receives an error response with status code {int}")
    public void the_user_receives_an_error_response_with_status_code(int expectedStatus) {
        var responseStatus = contextHolder.get(STATUS_CODE_KEY);
        assertThat(responseStatus).isEqualTo(expectedStatus);
    }

}
