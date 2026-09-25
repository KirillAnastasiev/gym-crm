package com.kirill.projects.gymcrm.cucumber.steps;

import com.kirill.projects.gymcrm.cucumber.client.ApiClient;
import com.kirill.projects.gymcrm.cucumber.dto.TrainingStatisticsResponse;
import com.kirill.projects.gymcrm.cucumber.util.BearerAuthenticationHeaderResolver;
import com.kirill.projects.gymcrm.cucumber.util.ContextHolder;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.kirill.projects.gymcrm.cucumber.util.ContextHolder.Key;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class TrainingStatisticsForTrainerTestFeatureSteps {
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


    // ==================== TRAINING STATISTICS FOR TRAINER TEST STEPS ====================

    @When("The user attempts to retrieve training statistics for a trainer {string}")
    public void the_user_attempts_to_retrieve_training_statistics_for_a_trainer(String trainerUsername) {
        contextHolder.put(USERNAME_KEY, trainerUsername);
    }

    @When("Send a {string} request to the training statistics endpoint {string}")
    public void send_a_request_to_the_training_statistics_endpoint(String httpMethod, String endpoint) throws IOException, InterruptedException {
        var trainerUsername = contextHolder.get(USERNAME_KEY);
        assertThat(endpoint).endsWith(trainerUsername);
        var accessToken = contextHolder.get(ACCES_TOKEN_KEY);
        try (var client = apiClient.spec()) {
            var authHeader = BearerAuthenticationHeaderResolver.resolve(accessToken);
            var request = apiClient.request(endpoint, httpMethod, Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader), null);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
            contextHolder.put(RESPONSE_BODY_KEY, response.body());
        }
    }

    @Then("The response contains the training statistics for the trainer {string}")
    public void the_response_contains_the_training_statistics_for_the_trainer(String trainerUsername) {
        var responseBody = contextHolder.get(RESPONSE_BODY_KEY);
        var statistics = jsonMapper.readValue(responseBody, TrainingStatisticsResponse.class);
        assertThat(statistics).isNotNull();
        assertThat(statistics).extracting(TrainingStatisticsResponse::trainerUsername).isEqualTo(trainerUsername);
    }

}
