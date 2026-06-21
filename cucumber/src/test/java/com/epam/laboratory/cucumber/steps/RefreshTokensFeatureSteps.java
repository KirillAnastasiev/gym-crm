package com.epam.laboratory.cucumber.steps;

import com.epam.laboratory.cucumber.client.ApiClient;
import com.epam.laboratory.cucumber.dto.RefreshTokenRequest;
import com.epam.laboratory.cucumber.dto.TokensResponse;
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

public class RefreshTokensFeatureSteps {
    private static final Key<String> REFRESH_TOKEN_KEY = Key.of("refreshToken", String.class);
    private static final Key<Integer> STATUS_CODE_KEY = Key.of("statusCode", Integer.class);
    private static final Key<String> RESPONSE_BODY_KEY = Key.of("responseBody", String.class);

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private TestUserAuthenticator authenticator;


    // ==================== REFRESH TOKENS TEST STEPS SUCCESSFUL ====================

    @Given("The user has a valid refresh token")
    public void the_user_has_a_valid_refresh_token() {
        var tokens = contextHolder.get(TestUserAuthenticator.TOKENS_KEY);
        assertThat(tokens).isNotNull();
        var refreshToken = tokens.refreshToken();
        assertThat(refreshToken).isNotBlank();
        contextHolder.put(REFRESH_TOKEN_KEY, refreshToken);
    }

    @When("The user attempts to refresh the access token by sending a {string} request to the token refresh endpoint {string}")
    public void the_user_attempts_to_refresh_the_access_token_by_sending_a_request_to_the_token_refresh_endpoint(String httpMethod, String endpoint) throws IOException, InterruptedException {
        var refreshTokenRequest = new RefreshTokenRequest(contextHolder.get(REFRESH_TOKEN_KEY));
        var requestBody = jsonMapper.writeValueAsString(refreshTokenRequest);
        try (var httpClient = apiClient.spec()) {
            var request = apiClient.request(endpoint, httpMethod, Map.of(ApiClient.CONTENT_TYPE_HEADER, "application/json"),  requestBody);
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
            contextHolder.put(RESPONSE_BODY_KEY, response.body());
        }
    }

    @Then("The user receives a successful response with status code {int}")
    public void the_user_receives_a_successful_response_with_status_code(int expectedStatus) {
        var actualStatus = contextHolder.get(STATUS_CODE_KEY);
        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @Then("The user receives a new access and refresh JWT tokens")
    public void the_user_receives_a_new_access_and_refresh_jwt_tokens() {
        var responseBody = contextHolder.get(RESPONSE_BODY_KEY);
        var tokens = jsonMapper.readValue(responseBody, TokensResponse.class);
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
    }


    // ==================== REFRESH TOKENS TEST STEPS FAILURE ====================

    @Given("The user has an invalid refresh token")
    public void the_user_has_an_invalid_refresh_token() {
        var invalidRefreshToken = "invalid-refresh-token";
        contextHolder.put(REFRESH_TOKEN_KEY, invalidRefreshToken);
    }

    @Then("The user receives an error response with a status code {int}")
    public void the_user_receives_an_error_response_with_a_status_code(int expectedStatus) {
        var responseStatus = contextHolder.get(STATUS_CODE_KEY);
        assertThat(responseStatus).isEqualTo(expectedStatus);
    }

}
