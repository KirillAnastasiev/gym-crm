package com.epam.laboratory.cucumber.steps;

import com.epam.laboratory.cucumber.client.ApiClient;
import com.epam.laboratory.cucumber.dto.ChangeStatusRequest;
import com.epam.laboratory.cucumber.util.BearerAuthenticationHeaderResolver;
import com.epam.laboratory.cucumber.util.ContextHolder;
import com.epam.laboratory.cucumber.util.ContextHolder.Key;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ChangeTraineeStatusTestFeatureSteps {
    private static final Key<String> USERNAME_KEY = Key.of("username", String.class);
    private static final Key<String> ACCESS_TOKEN_KEY = Key.of("accessToken", String.class);
    private static final Key<Integer> STATUS_CODE_KEY = Key.of("statusCode", Integer.class);

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JsonMapper jsonMapper;


    // ==================== CHANGE TRAINEE STATUS TEST STEPS SUCCESSFUL ====================

    @When("The user attempts to change the trainee's status to {string} by sending a {string} request to the endpoint {string}")
    public void the_user_attempts_to_change_the_trainee_s_status_to_by_sending_a_request_to_the_endpoint(String activity, String httpStatus, String endpoint) throws IOException, InterruptedException {
        var username = contextHolder.get(USERNAME_KEY);
        assertThat(endpoint).endsWith(username);
        var isActive = Boolean.valueOf(activity);
        var accessToken = contextHolder.get(ACCESS_TOKEN_KEY);
        var changeStatusRequest = new ChangeStatusRequest(isActive);
        try (var client = apiClient.spec()) {
            var authHeader = BearerAuthenticationHeaderResolver.resolve(accessToken);
            var requestBody = jsonMapper.writeValueAsString(changeStatusRequest);
            var httpHeaders = Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader, ApiClient.CONTENT_TYPE_HEADER, "application/json");
            var request = apiClient.request(endpoint, httpStatus, httpHeaders, requestBody);
            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
        }
    }

}
