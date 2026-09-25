package com.kirill.projects.gymcrm.cucumber.steps;

import com.kirill.projects.gymcrm.cucumber.client.ApiClient;
import com.kirill.projects.gymcrm.cucumber.dto.Training;
import com.kirill.projects.gymcrm.cucumber.dto.TrainingType;
import com.kirill.projects.gymcrm.cucumber.util.BearerAuthenticationHeaderResolver;
import com.kirill.projects.gymcrm.cucumber.util.ContextHolder;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static com.kirill.projects.gymcrm.cucumber.util.ContextHolder.Key;

public class RegisterTrainingTestFeatureSteps {
    private static final Key<String> ACCES_TOKEN_KEY = Key.of("accessToken", String.class);
    private static final Key<Training> ADD_TRAINING_REQUEST_KEY = Key.of("addTrainingRequest", Training.class);
    private static final Key<Integer> STATUS_CODE_KEY = Key.of("statusCode", Integer.class);


    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JsonMapper jsonMapper;


    // ==================== REGISTER TRAINING TEST STEPS ====================

    @When("The user attempts to register a new training with trainee {string}, trainer {string}, training name {string}, training type {string}, training date {string} and duration {int}")
    public void the_user_attempts_to_register_a_new_training_with_details(String trainee, String trainer, String trainingName, String trainingType, String dateString, int duration) {
        var trainingTypeArgs = trainingType.split(":");
        var type = new TrainingType(Long.parseLong(trainingTypeArgs[0]), trainingTypeArgs[1]);
        var trainingDate = LocalDateTime.parse(dateString);
        var trainingDuration = Duration.ofMinutes(duration);
        var trainingRequest = new Training(trainee, trainer, trainingName, type, trainingDate, trainingDuration);
        contextHolder.put(ADD_TRAINING_REQUEST_KEY, trainingRequest);
    }

    @When("Send a {string} request to the register training endpoint {string}")
    public void send_a_request_to_the_register_training_endpoint(String httpMethod, String endpoint) throws IOException, InterruptedException {
        var accessToken = contextHolder.get(ACCES_TOKEN_KEY);
        var trainingRequest = contextHolder.get(ADD_TRAINING_REQUEST_KEY);
        try (var client = apiClient.spec()) {
            var authHeader = BearerAuthenticationHeaderResolver.resolve(accessToken);
            var requestBody = jsonMapper.writeValueAsString(trainingRequest);
            var httpHeaders = Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader, ApiClient.CONTENT_TYPE_HEADER, "application/json");
            var request = apiClient.request(endpoint, httpMethod, httpHeaders, requestBody);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
        }
    }

}
