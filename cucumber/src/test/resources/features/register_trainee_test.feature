Feature: Register Trainee Feature

  Scenario Outline: The user registers a trainee with valid details
    Given The user has valid trainee registration details: first name "<first_name>", last name "<last_name>", date of birth "<date_of_birth>", and address "<address>"
    When The user attempts to register a trainee by sending a "POST" request to the registration endpoint "/api/trainees"
    Then The user receives a successful response with status code 201
    And The user receives a response body containing the registered trainee's username "<username>" and generated password

    Examples:
      | first_name | last_name | date_of_birth | address  | username             |
      | FirstName1 | LastName1 | 1990-01-01    | Address1 | FirstName1.LastName1 |
      | FirstName2 | LastName2 | 1995-05-15    | Address2 | FirstName2.LastName2 |


  Scenario Outline: The user registers a trainee with missing required fields
    Given The user has incomplete trainee registration details: first name "<first_name>", last name "<last_name>", date of birth "<date_of_birth>", and address "<address>"
    When The user attempts to register a trainee by sending a "POST" request to the registration endpoint "/api/trainees"
    Then The user receives an error response with status code 400

    Examples:
      | first_name | last_name | date_of_birth | address  |
      |            | LastName1 | 1990-01-01    | Address1 |
      | FirstName2 |           | 1995-05-15    | Address2 |
