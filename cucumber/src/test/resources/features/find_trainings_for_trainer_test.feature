Feature: Find Trainings for Trainer Feature

  Scenario: The user successfully finds trainings for a trainer
    Given The user is authenticated with authenticates with valid JWT token
    When The user attempts to find trainings for trainer "Sarah.Davis"
    And Send a "GET" request to the find trainings for trainer endpoint "/api/trainings/trainer/Sarah.Davis"
    Then The user receives a response with status code 200
    And The user receives a response containing a list of trainings for trainer "Sarah.Davis"


  Scenario: The user fails to find trainings for a non-existent trainer
    Given The user is authenticated with authenticates with valid JWT token
    When The user attempts to find trainings for trainer "NonExistentUser"
    And Send a "GET" request to the find trainings for trainer endpoint "/api/trainings/trainer/NonExistentUser"
    Then The user receives a response with status code 200
    And The user receives empty list