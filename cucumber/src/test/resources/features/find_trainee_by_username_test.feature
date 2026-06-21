Feature: Find Trainee By Username Feature

  Scenario Outline: The user finds a trainee by username with a valid username
    Given The trainee with a valid trainee username "<username>" exists in the system
    And The user is authenticated with authenticates with valid JWT token
    When The user attempts to find the trainee by sending a "GET" request to the endpoint "<endpoint>"
    Then The user receives a successful response with status code 200
    And The user receives a response body containing the trainee's details including "<first_name>", "<last_name>" and "<username>"

    Examples:
        | username      | endpoint                    | first_name | last_name |
        | John.Doe      | /api/trainees/John.Doe      | John       | Doe       |
        | Jane.Smith    | /api/trainees/Jane.Smith    | Jane       | Smith     |
        | Emily.Johnson | /api/trainees/Emily.Johnson | Emily      | Johnson   |
        | Michael.Brown | /api/trainees/Michael.Brown | Michael    | Brown     |


  Scenario: The user finds a trainee by username with an invalid username
    Given The trainee with a username "Invalid.Username" doesn't exist in the system
    And The user is authenticated with authenticates with valid JWT token
    When The user attempts to find the trainee by sending a "GET" request to the endpoint "/api/trainees/Invalid.Username"
    Then The user receives an error response with status code 404