Feature: Update Trainee Profile Feature

  Scenario Outline: The user updates a trainee profile with valid details
    Given The trainee with a valid trainee username "<username>" exists in the system
    And The user is authenticated with authenticates with valid JWT token
    When The user updates trainee details: first name "<first_name>", last name "<last_name>", date of birth "<date_of_birth>", address "<address>", and active status "<is_active>"
    And Attempts to update the trainee profile by sending a "PUT" request to the endpoint "<endpoint>"
    Then The user receives a successful response with status code 200
    And The user receives a response body containing the updated trainee's details including first name "<first_name>", last name "<last_name>", username "<username>", address "<address>", date of birth "<date_of_birth>", and active status "<is_active>"

    Examples:
        | username      | endpoint                    | first_name  | last_name  | date_of_birth | address     | is_active |
        | Jane.Smith    | /api/trainees/Jane.Smith    | Jane1       | Smith1     | 1995-05-15    | NewAddress2 | false     |
        | Emily.Johnson | /api/trainees/Emily.Johnson | Emily1      | Johnson1   | 1988-03-10    | NewAddress3 | false     |
        | Michael.Brown | /api/trainees/Michael.Brown | Michael1    | Brown1     | 1992-07-20    | NewAddress4 | false     |
