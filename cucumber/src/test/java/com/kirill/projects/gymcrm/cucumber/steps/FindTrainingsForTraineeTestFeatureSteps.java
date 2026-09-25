package com.kirill.projects.gymcrm.cucumber.steps;

import com.kirill.projects.gymcrm.cucumber.client.ApiClient;
import com.kirill.projects.gymcrm.cucumber.dto.Training;
import com.kirill.projects.gymcrm.cucumber.util.BearerAuthenticationHeaderResolver;
import com.kirill.projects.gymcrm.cucumber.util.ContextHolder;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

import static com.kirill.projects.gymcrm.cucumber.util.ContextHolder.Key;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FindTrainingsForTraineeTestFeatureSteps {
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


    // ==================== FIND TRAINING FOR TRAINEE TEST STEPS SUCCESSFUL ====================

    @When("The user attempts to find trainings for trainee {string}")
    public void the_user_attempts_to_find_trainings_for_trainee(String username) {
        contextHolder.put(USERNAME_KEY, username);
    }

    @When("Send a {string} request to the find trainings for trainee endpoint {string}")
    public void send_a_request_to_the_find_trainings_for_trainee_endpoint(String httpMethod, String endpoint) throws IOException, InterruptedException {
        var traineeUsername = contextHolder.get(USERNAME_KEY);
        assertThat(endpoint).endsWith(traineeUsername);
        var accessToken = contextHolder.get(ACCES_TOKEN_KEY);
        try (var client = apiClient.spec()) {
            var authHeader = BearerAuthenticationHeaderResolver.resolve(accessToken);
            var request = apiClient.request(endpoint, httpMethod, Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader), null);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
            contextHolder.put(RESPONSE_BODY_KEY, response.body());
        }
    }

    @Then("The user receives a response containing a list of trainings for trainee {string}")
    public void the_user_receives_a_response_containing_a_list_of_trainings_for_trainee(String username) {
        var responseBody = contextHolder.get(RESPONSE_BODY_KEY);
        assertThat(responseBody).isNotNull();
        var typeDef = jsonMapper.getTypeFactory().constructCollectionType(Collection.class, Training.class);
        Collection<Training> trainings = jsonMapper.readValue(responseBody, typeDef);
        assertThat(trainings).isNotNull();
        assertThat(trainings).isNotEmpty();
        trainings.forEach(training -> assertThat(training).extracting(Training::traineeUsername).isEqualTo(username));
    }


    // ==================== FIND TRAINING FOR TRAINEE TEST STEPS FAILURE ====================

    @Then("The user receives empty list")
    public void the_user_receives_empty_list() {
        var responseBody = contextHolder.get(RESPONSE_BODY_KEY);
        assertThat(responseBody).isNotNull();
        var typeDef = jsonMapper.getTypeFactory().constructCollectionType(Collection.class, Training.class);
        Collection<Training> trainings = jsonMapper.readValue(responseBody, typeDef);
        assertThat(trainings).isNotNull();
        assertThat(trainings).isEmpty();
    }

}
