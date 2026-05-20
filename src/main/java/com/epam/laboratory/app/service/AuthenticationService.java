package com.epam.laboratory.app.service;

import java.util.Map;

public interface AuthenticationService {
    boolean checkExistsByUsername(String username);
    boolean checkPasswordForUsername(String username, String password);
    void validateUser(String username, String password);
    Map<String, String> getUserTokens(String username);
    Map<String, String> refreshAccessToken(String refreshToken);
    void changePassword(String username, String oldPassword, String newPassword);
}
