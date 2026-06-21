Feature: Refresh Tokens Feature

  Scenario: The user refreshes access tokens using a valid refresh token
    Given The user has a valid refresh token
    When The user attempts to refresh the access token by sending a "POST" request to the token refresh endpoint "/api/auth/refresh-token"
    Then The user receives a successful response with status code 200
    And The user receives a new access and refresh JWT tokens


  Scenario: The user refreshes access tokens using an invalid refresh token
    Given The user has an invalid refresh token
    When The user attempts to refresh the access token by sending a "POST" request to the token refresh endpoint "/api/auth/refresh-token"
    Then The user receives an error response with a status code 400