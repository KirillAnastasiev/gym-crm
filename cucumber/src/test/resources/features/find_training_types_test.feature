Feature: Find All Training Types Feature

  Scenario: The user successfully retrieves the list of training types
    Given The user is authenticated with authenticates with valid JWT token
    When Send a "GET" request to the training types endpoint "/api/training-types"
    Then The user receives a response with status code 200
    And The response contains a list of training types
