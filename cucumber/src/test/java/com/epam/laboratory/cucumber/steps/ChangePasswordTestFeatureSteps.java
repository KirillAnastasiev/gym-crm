package com.epam.laboratory.cucumber.steps;

import com.epam.laboratory.cucumber.client.ApiClient;
import com.epam.laboratory.cucumber.dto.ChangePasswordRequest;
import com.epam.laboratory.cucumber.dto.Credentials;
import com.epam.laboratory.cucumber.util.BasicAuthenticationHeaderResolver;
import com.epam.laboratory.cucumber.util.BearerAuthenticationHeaderResolver;
import com.epam.laboratory.cucumber.util.ContextHolder;
import com.epam.laboratory.cucumber.util.ContextHolder.Key;
import com.epam.laboratory.cucumber.util.TestUserAuthenticator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ChangePasswordTestFeatureSteps {
    private static final Key<String> ACCESS_TOKEN_KEY = Key.of("accessToken", String.class);
    private static final Key<ChangePasswordRequest> CHANGE_PASSWORD_REQUEST_KEY = Key.of("changePasswordRequest", ChangePasswordRequest.class);
    private static final Key<Integer> STATUS_CODE_KEY = Key.of("statusCode", Integer.class);

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private TestUserAuthenticator authenticator;


    // ==================== CHANGE PASSWORD TEST STEPS SUCCESSFUL ====================

    @Given("The user is authenticated with authenticates with username {string} and password {string}")
    public void the_user_is_authenticated_with_authenticates_with_username_and_password(String username, String password) throws IOException, InterruptedException {
        var credentials = new Credentials(username, password);
        try (var client = apiClient.spec()) {
            var authHeader = BasicAuthenticationHeaderResolver.resolve(credentials);
            var authRequest = apiClient.request("/api/auth/tokens", ApiClient.HTTP_METHOD_GET, Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader), null);
            var response = client.send(authRequest, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            var tokensResponse = jsonMapper.readValue(response.body(), com.epam.laboratory.cucumber.dto.TokensResponse.class);
            var accessToken = tokensResponse.accessToken();
            assertThat(accessToken).isNotBlank();
            contextHolder.put(ACCESS_TOKEN_KEY, accessToken);
        }
    }

    @When("The user attempts to change their password {string} with {string}")
    public void the_user_attempts_to_change_their_current_password_with_new_password(String oldPassword, String newPassword) {
        var changePasswordRequest = new ChangePasswordRequest(oldPassword, newPassword);
        contextHolder.put(CHANGE_PASSWORD_REQUEST_KEY, changePasswordRequest);
    }

    @When("Send a {string} request to the change password endpoint {string}")
    public void send_a_request_to_the_change_password_endpoint(String httpMethod, String endpoint) throws IOException, InterruptedException {
        var changePasswordRequest = contextHolder.get(CHANGE_PASSWORD_REQUEST_KEY);
        try (var client = apiClient.spec()) {
            var authHeader = BearerAuthenticationHeaderResolver.resolve(contextHolder.get(ACCESS_TOKEN_KEY));
            var requestBody = jsonMapper.writeValueAsString(changePasswordRequest);
            var request = apiClient.request(endpoint, httpMethod, Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader, ApiClient.CONTENT_TYPE_HEADER, "application/json"), requestBody);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
        }
    }

    @Then("The user receives a response with status code {int}")
    public void the_user_receives_a_response_with_status_code_status_code(int expectedStatus) {
        var actualStatus = contextHolder.get(STATUS_CODE_KEY);
        assertThat(actualStatus).isEqualTo(expectedStatus);
    }


    // ==================== CHANGE PASSWORD TEST STEPS FAILURE ====================

    @When("The user attempts to change wrong password {string} with {string}")
    public void the_user_attempts_to_change_wrong_password_with(String wrongPassword, String newPassword) {
        var changePasswordRequest = new ChangePasswordRequest(wrongPassword, newPassword);
        contextHolder.put(CHANGE_PASSWORD_REQUEST_KEY, changePasswordRequest);
    }

}
