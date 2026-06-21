Feature: Find Trainings for Trainee Feature

  Scenario: The user successfully finds trainings for a trainee
    Given The user is authenticated with authenticates with valid JWT token
    When The user attempts to find trainings for trainee "John.Doe"
    And Send a "GET" request to the find trainings for trainee endpoint "/api/trainings/trainee/John.Doe"
    Then The user receives a response with status code 200
    And The user receives a response containing a list of trainings for trainee "John.Doe"


  Scenario: The user fails to find trainings for a non-existent trainee
    Given The user is authenticated with authenticates with valid JWT token
    When The user attempts to find trainings for trainee "NonExistentUser"
    And Send a "GET" request to the find trainings for trainee endpoint "/api/trainings/trainee/NonExistentUser"
    Then The user receives a response with status code 200
    And The user receives empty list