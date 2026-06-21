Feature: Register New Training Feature

  Scenario Outline: The user successfully registers a new training
    Given The user is authenticated with authenticates with valid JWT token
    When The user attempts to register a new training with trainee "<trainee_username>", trainer "<trainer_username>", training name "<training_name>", training type "<training_type>", training date "<training_date>" and duration <duration>
    And Send a "POST" request to the register training endpoint "/api/trainings"
    Then The user receives a response with status code 201

    Examples:
      | trainee_username | trainer_username | training_name    | training_type | training_date       | duration |
      | John.Doe         | Sarah.Davis      | Soft yoga        | 2:Yoga        | 2025-12-15T12:45:00 | 30       |
      | Jane.Smith       | David.Wilson     | Strength Fitness | 1:Fitness     | 2025-08-22T10:30:00 | 45       |
      | Emily.Johnson    | Laura.Miller     | Power Crossfit   | 5:Crossfit    | 2025-10-05T14:00:00 | 60       |


  Scenario Outline: The user fails to register a new training due to missing required fields
    Given The user is authenticated with authenticates with valid JWT token
    When The user attempts to register a new training with trainee "<trainee_username>", trainer "<trainer_username>", training name "<training_name>", training type "<training_type>", training date "<training_date>" and duration <duration>
    And Send a "POST" request to the register training endpoint "/api/trainings"
    Then The user receives a response with status code 400

    Examples:
      | trainee_username | trainer_username | training_name    | training_type | training_date       | duration |
      |                  | Sarah.Davis      | Soft yoga        | 2:Yoga        | 2025-12-15T12:45:00 | 30       |
      | Jane.Smith       |                  | Strength Fitness | 1:Fitness     | 2025-08-22T10:30:00 | 45       |
      | Emily.Johnson    | Laura.Miller     |                  | 5:Crossfit    | 2025-10-05T14:00:00 | 60       |