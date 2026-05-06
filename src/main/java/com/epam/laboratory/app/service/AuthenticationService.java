package com.epam.laboratory.app.service;

public interface AuthenticationService {
    boolean checkExistsByUsername(String username);
    boolean checkPasswordForUsername(String username, String password);
    void validateUser(String username, String password);
    void changePassword(String username, String oldPassword, String newPassword);
}
