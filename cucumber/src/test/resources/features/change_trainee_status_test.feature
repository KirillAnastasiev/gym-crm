Feature: Change Trainee Status Feature

  Scenario Outline: The user changes a trainee's active status successfully
    Given The trainee with a valid trainee username "<username>" exists in the system
    And The user is authenticated with authenticates with valid JWT token
    When The user attempts to change the trainee's status to "<is_active>" by sending a "PATCH" request to the endpoint "<endpoint>"
    Then The user receives a successful response with status code 200

    Examples:
        | username      | endpoint                    | is_active |
        | Jane.Smith    | /api/trainees/Jane.Smith    | false     |
        | Emily.Johnson | /api/trainees/Emily.Johnson | true      |
        | Michael.Brown | /api/trainees/Michael.Brown | false     |