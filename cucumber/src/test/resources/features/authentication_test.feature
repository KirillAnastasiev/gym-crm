Feature: Authentication Feature

  Scenario Outline: The user logs in with valid credentials and receive JWT tokens
    Given The user has valid credentials with username "<username>" and password "<password>"
    When The user attempts to log in by sending a "GET" request to the authentication endpoint "/api/auth/tokens" with the provided credentials
    Then The the user receives a successful response with status code 200
    And The user receives access and refresh JWT tokens

    Examples:
      | username      | password    |
      | John.Doe      | password123 |
      | Jane.Smith    | password456 |
      | Emily.Johnson | password789 |
      | Michael.Brown | password321 |
      | Sarah.Davis   | password654 |
      | David.Wilson  | password987 |
      | Laura.Miller  | password111 |
      | James.Taylor  | password222 |


  Scenario Outline: The user fails to log in with invalid credentials and receives an error response
    Given The user has invalid credentials with username "<username>" and password "<password>"
    When The user attempts to log in by sending a "GET" request to the authentication endpoint "/api/auth/tokens" with the provided credentials
    Then The user receives an error response with status code 401

    Examples:
      | username         | password    |
      | John.Doe         | wrongpass   |
      | Unknown.Username | password123 |
      | Unknown.Username | wrongpass   |

