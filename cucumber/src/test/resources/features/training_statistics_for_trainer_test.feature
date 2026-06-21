Feature: Training Statistics for Trainer Feature

  Scenario: The user successfully retrieves training statistics for a trainer
    Given The user is authenticated with authenticates with valid JWT token
    When The user attempts to retrieve training statistics for a trainer "Sarah.Davis"
    When Send a "GET" request to the training statistics endpoint "/api/statistics/Sarah.Davis"
    Then The user receives a response with status code 200
    And The response contains the training statistics for the trainer "Sarah.Davis"