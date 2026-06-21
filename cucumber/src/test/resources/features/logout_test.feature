Feature: Logout Feature

  Scenario: The user successfully logs out
    Given The user is authenticated with authenticates with valid JWT token
    When The user attempts to log out sending a "POST" request to the logout endpoint "/api/auth/logout"
    Then The user receives a response with status code 200
