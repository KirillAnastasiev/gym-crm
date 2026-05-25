package com.epam.laboratory.app.repository;


public interface AuthenticationDao {
    boolean checkExistsByUsername(String username);
    boolean checkPasswordForUsername(String userName, String password);
    void changePasswordForUsername(String username, String newPassword);
}
