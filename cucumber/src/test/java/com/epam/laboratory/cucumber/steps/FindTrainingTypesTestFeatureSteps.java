package com.epam.laboratory.cucumber.steps;

import com.epam.laboratory.cucumber.client.ApiClient;
import com.epam.laboratory.cucumber.dto.TrainingType;
import com.epam.laboratory.cucumber.util.BearerAuthenticationHeaderResolver;
import com.epam.laboratory.cucumber.util.ContextHolder;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class FindTrainingTypesTestFeatureSteps {
    private static final ContextHolder.Key<String> ACCES_TOKEN_KEY = ContextHolder.Key.of("accessToken", String.class);
    private static final ContextHolder.Key<Integer> STATUS_CODE_KEY = ContextHolder.Key.of("statusCode", Integer.class);
    private static final ContextHolder.Key<String> RESPONSE_BODY_KEY = ContextHolder.Key.of("responseBody", String.class);

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JsonMapper jsonMapper;


    // ==================== FIND TRAINING TYPES TEST STEPS SUCCESSFUL ====================

    @When("Send a {string} request to the training types endpoint {string}")
    public void send_a_request_to_the_training_types_endpoint(String httpMethod, String endpoint) throws IOException, InterruptedException {
        var accessToken = contextHolder.get(ACCES_TOKEN_KEY);
        try (var client = apiClient.spec()) {
            var authHeader = BearerAuthenticationHeaderResolver.resolve(accessToken);
            var request = apiClient.request(endpoint, httpMethod, java.util.Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader), null);
            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
            contextHolder.put(RESPONSE_BODY_KEY, response.body());
        }
    }

    @Then("The response contains a list of training types")
    public void the_response_contains_a_list_of_training_types() {
        var responseBody = contextHolder.get(RESPONSE_BODY_KEY);
        var typeDef = jsonMapper.getTypeFactory().constructCollectionType(Collection.class, TrainingType.class);
        Collection<TrainingType> trainingTypes = jsonMapper.readValue(responseBody, typeDef);
        assertThat(trainingTypes).isNotNull();
        assertThat(trainingTypes).isNotEmpty();
        trainingTypes.forEach(trainingType -> {
            assertThat(trainingType).isNotNull();
            assertThat(trainingType).extracting(TrainingType::trainingTypeName).isNotNull();
        });
    }

}
