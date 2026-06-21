package com.epam.laboratory.cucumber.steps;

import com.epam.laboratory.cucumber.client.ApiClient;
import com.epam.laboratory.cucumber.dto.Trainee;
import com.epam.laboratory.cucumber.util.BearerAuthenticationHeaderResolver;
import com.epam.laboratory.cucumber.util.ContextHolder;
import com.epam.laboratory.cucumber.util.ContextHolder.Key;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FindTraineeByUsernameTestFeatureSteps {
    private static final Key<String> USERNAME_KEY = Key.of("username", String.class);
    private static final Key<String> ACCES_TOKEN_KEY = Key.of("accessToken", String.class);
    private static final Key<Integer> STATUS_CODE_KEY = Key.of("statusCode", Integer.class);
    private static final Key<String> RESPONSE_BODY_KEY = Key.of("responseBody", String.class);

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JsonMapper jsonMapper;


    // ==================== FIND TRAINEE BY USERNAME TEST STEPS SUCCESSFUL ====================

    @Given("The trainee with a valid trainee username {string} exists in the system")
    public void the_trainee_with_a_valid_trainee_username_exists_in_the_system(String username) {
        contextHolder.put(USERNAME_KEY, username);
    }

    @When("The user attempts to find the trainee by sending a {string} request to the endpoint {string}")
    public void the_user_attempts_to_find_the_trainee_by_sending_a_request_to_the_endpoint(String httpMethod, String endpoint) throws IOException, InterruptedException {
        var username = contextHolder.get(USERNAME_KEY);
        assertThat(endpoint.endsWith(username)).isTrue();
        var accessToken = contextHolder.get(ACCES_TOKEN_KEY);
        try (var client = apiClient.spec()) {
            var authHeader = BearerAuthenticationHeaderResolver.resolve(accessToken);
            var request = apiClient.request(endpoint, httpMethod, Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader), null);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
            contextHolder.put(RESPONSE_BODY_KEY, response.body());
        }
    }

    @Then("The user receives a response body containing the trainee's details including {string}, {string} and {string}")
    public void the_user_receives_a_response_body_containing_the_trainee_s_details_including_and(String firstName, String lastName, String username) {
        var responseBody = contextHolder.get(RESPONSE_BODY_KEY);
        assertThat(responseBody).isNotNull();
        var trainee = jsonMapper.readValue(responseBody, Trainee.class);
        assertThat(trainee).isNotNull();
        assertThat(trainee).extracting(Trainee::username).isEqualTo(username);
        assertThat(trainee).extracting(Trainee::firstName).isEqualTo(firstName);
        assertThat(trainee).extracting(Trainee::lastName).isEqualTo(lastName);
    }


    // ==================== FIND TRAINEE BY USERNAME TEST STEPS FAILURE ====================

    @Given("The trainee with a username {string} doesn't exist in the system")
    public void the_trainee_with_a_username_not_exists_in_the_system(String username) {
        contextHolder.put(USERNAME_KEY, username);
    }

}
