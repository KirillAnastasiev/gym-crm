Feature: Change Password Feature

  Scenario Outline: The user successfully changes their password
    Given The user is authenticated with authenticates with username "<username>" and password "<password>"
    When The user attempts to change their password "<old_password>" with "<new_password>"
    And Send a "PUT" request to the change password endpoint "/api/auth/change-password"
    Then The user receives a response with status code 200

    Examples:
      | username     | password    | old_password | new_password   |
      | James.Taylor | password222 | password222  | newpassword222 |


  Scenario Outline: The user fails to change their password due to incorrect old password
    Given The user is authenticated with authenticates with username "<username>" and password "<password>"
    When The user attempts to change wrong password "<old_password>" with "<new_password>"
    And Send a "PUT" request to the change password endpoint "/api/auth/change-password"
    Then The user receives a response with status code 400

    Examples:
      | username     | password    | old_password | new_password   |
      | Laura.Miller | password111 | wrongpass    | newpassword222 |