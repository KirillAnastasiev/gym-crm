package com.kirill.projects.gymcrm.cucumber.steps;

import com.kirill.projects.gymcrm.cucumber.client.ApiClient;
import com.kirill.projects.gymcrm.cucumber.dto.Trainee;
import com.kirill.projects.gymcrm.cucumber.dto.UpdateTraineeRequest;
import com.kirill.projects.gymcrm.cucumber.util.BearerAuthenticationHeaderResolver;
import com.kirill.projects.gymcrm.cucumber.util.ContextHolder;
import com.kirill.projects.gymcrm.cucumber.util.TestUserAuthenticator;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.kirill.projects.gymcrm.cucumber.util.ContextHolder.Key;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UpdateTraineeProfileTestFeatureSteps {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Key<String> USERNAME_KEY = Key.of("username", String.class);
    private static final Key<UpdateTraineeRequest>  UPDATE_TRAINEE_REQUEST_KEY = Key.of("updateTraineeRequest", UpdateTraineeRequest.class);
    private static final Key<Integer> STATUS_CODE_KEY = Key.of("statusCode", Integer.class);
    private static final Key<String> RESPONSE_BODY_KEY = Key.of("responseBody", String.class);

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JsonMapper jsonMapper;


    // ==================== UPDATE TRAINEE TEST STEPS SUCCESSFUL ====================

    @When("The user updates trainee details: first name {string}, last name {string}, date of birth {string}, address {string}, and active status {string}")
    public void the_user_updates_trainee_details_first_name_last_name_date_of_birth_address_and_active_status(String firstName, String lastName, String dateString, String address, String isActive) {
        var username = contextHolder.get(USERNAME_KEY);
        assertThat(username).isNotEmpty();
        var dateOfBirth = LocalDate.parse(dateString, DATE_FORMATTER);
        var status = Boolean.valueOf(isActive);
        var updateTraineeRequest = new UpdateTraineeRequest(firstName, lastName, dateOfBirth, address, status);
        contextHolder.put(UPDATE_TRAINEE_REQUEST_KEY, updateTraineeRequest);
    }

    @When("Attempts to update the trainee profile by sending a {string} request to the endpoint {string}")
    public void attempts_to_update_the_trainee_profile_by_sending_a_request_to_the_endpoint(String httpMethod, String endpoint) throws IOException, InterruptedException {
        var updateTraineeRequest = contextHolder.get(UPDATE_TRAINEE_REQUEST_KEY);
        var tokens = contextHolder.get(TestUserAuthenticator.TOKENS_KEY);
        var accessToken = tokens.accessToken();
        try (var client = apiClient.spec()) {
            var requestBody = jsonMapper.writeValueAsString(updateTraineeRequest);
            var authHeader = BearerAuthenticationHeaderResolver.resolve(accessToken);
            var httpHeaders = Map.of(ApiClient.CONTENT_TYPE_HEADER, "application/json", ApiClient.AUTHORIZATION_HEADER, authHeader);
            var request = apiClient.request(endpoint, httpMethod, httpHeaders, requestBody);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
            contextHolder.put(RESPONSE_BODY_KEY, response.body());
        }
    }

    @Then("The user receives a response body containing the updated trainee's details including first name {string}, last name {string}, username {string}, address {string}, date of birth {string}, and active status {string}")
    public void the_user_receives_a_response_body_containing_the_updated_trainee_s_details(String firstName, String lastName, String username, String address, String dateString, String activeStatus) {
        var responseBody = contextHolder.get(RESPONSE_BODY_KEY);
        var updatedTrainee = jsonMapper.readValue(responseBody, Trainee.class);
        var dateOfBirth = LocalDate.parse(dateString, DATE_FORMATTER);
        var isActive = Boolean.valueOf(activeStatus);
        assertThat(updatedTrainee).isNotNull();
        assertThat(updatedTrainee).extracting(Trainee::username).isEqualTo(username);
        assertThat(updatedTrainee).extracting(Trainee::firstName).isEqualTo(firstName);
        assertThat(updatedTrainee).extracting(Trainee::lastName).isEqualTo(lastName);
        assertThat(updatedTrainee).extracting(Trainee::address).isEqualTo(address);
        assertThat(updatedTrainee).extracting(Trainee::dateOfBirth).isEqualTo(dateOfBirth);
        assertThat(updatedTrainee).extracting(Trainee::active).isEqualTo(isActive);
    }


}
