package com.epam.laboratory.app.service.security;

import java.util.Map;

public interface AuthenticationService {
    boolean checkExistsByUsername(String username);
    Map<String, String> getUserTokens(String username);
    Map<String, String> refreshAccessToken(String refreshToken);
    void changePassword(String username, String oldPassword, String newPassword);
    void logout(String username);
}
