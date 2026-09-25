package com.kirill.projects.gymcrm.cucumber.steps;

import com.kirill.projects.gymcrm.cucumber.client.ApiClient;
import com.kirill.projects.gymcrm.cucumber.dto.RegisterTraineeRequest;
import com.kirill.projects.gymcrm.cucumber.dto.UserRegistrationResponse;
import com.kirill.projects.gymcrm.cucumber.util.ContextHolder;
import com.kirill.projects.gymcrm.cucumber.util.ContextHolder.Key;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class RegisterTraineeTestFeatureSteps {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Key<RegisterTraineeRequest> REGISTER_TRAINEE_REQUEST_KEY = Key.of("registerTraineeRequest", RegisterTraineeRequest.class);
    private static final Key<Integer> STATUS_CODE_KEY = Key.of("statusCode", Integer.class);
    private static final Key<String> RESPONSE_BODY_KEY = Key.of("responseBody", String.class);

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JsonMapper jsonMapper;


    // ==================== REGISTER TRAINEE TEST STEPS SUCCESSFUL ====================

    @Given("The user has valid trainee registration details: first name {string}, last name {string}, date of birth {string}, and address {string}")
    public void the_user_has_valid_trainee_registration_details(String firstName, String lastName, String dateString, String address) {
        var dateOfBirth = LocalDate.parse(dateString, DATE_FORMATTER);
        var registerTraineeRequest = new RegisterTraineeRequest(firstName, lastName, dateOfBirth, address);
        contextHolder.put(REGISTER_TRAINEE_REQUEST_KEY, registerTraineeRequest);
    }

    @When("The user attempts to register a trainee by sending a {string} request to the registration endpoint {string}")
    public void the_user_attempts_to_register_a_trainee_by_sending_a_request_to_the_registration_endpoint(String httpMethod, String endpoint) throws IOException, InterruptedException {
        var registerTraineeRequest = contextHolder.get(REGISTER_TRAINEE_REQUEST_KEY);
        try (var client = apiClient.spec()) {
            var requestBody = jsonMapper.writeValueAsString(registerTraineeRequest);
            var request = apiClient.request(endpoint, httpMethod, Map.of(ApiClient.CONTENT_TYPE_HEADER, "application/json"), requestBody);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            contextHolder.put(STATUS_CODE_KEY, response.statusCode());
            contextHolder.put(RESPONSE_BODY_KEY, response.body());
        }
    }

    @Then("The user receives a response body containing the registered trainee's username {string} and generated password")
    public void the_user_receives_a_response_body_containing_the_registered_trainee_s_username_and_generated_password(String expectedUsername) {
        var responseBody = contextHolder.get(RESPONSE_BODY_KEY);
        var userRegistrationResponse = jsonMapper.readValue(responseBody, UserRegistrationResponse.class);
        assertThat(userRegistrationResponse).isNotNull();
        assertThat(userRegistrationResponse.username().startsWith(expectedUsername)).isTrue();
        assertThat(userRegistrationResponse).extracting(UserRegistrationResponse::password).isNotNull();
    }


    // ==================== REGISTER TRAINEE TEST STEPS FAILURE ====================

    @Given("The user has incomplete trainee registration details: first name {string}, last name {string}, date of birth {string}, and address {string}")
    public void the_user_has_incomplete_trainee_registration_details_first_name_last_name_date_of_birth_and_address(String firstName, String lastName, String dateString, String address) {
        var dateOfBirth = LocalDate.parse(dateString, DATE_FORMATTER);
        var traineeRegistrationRequest = new RegisterTraineeRequest(firstName, lastName, dateOfBirth, address);
        contextHolder.put(REGISTER_TRAINEE_REQUEST_KEY, traineeRegistrationRequest);
    }

}
