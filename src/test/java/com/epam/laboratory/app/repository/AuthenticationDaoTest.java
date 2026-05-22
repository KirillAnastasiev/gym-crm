package com.epam.laboratory.app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
@Import(AuthenticationDaoImpl.class)
@DisplayName("AuthenticationDao test suite")
class AuthenticationDaoTest {

    @Autowired
    private AuthenticationDao authenticationDao;

    // ==================== CHECK EXISTS BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method checkExistsByUsername - should return true if user with given username exists")
    void testCheckExistsByUsername_positive() {
        // given
        var existingUsername = "John.Doe";

        // when
        var actualResult = authenticationDao.checkExistsByUsername(existingUsername);

        // then
        assertThat(actualResult).isTrue();
    }

    @Test
    @DisplayName("Test of the method checkExistsByUsername - should return false if user with given username does not exist")
    void testCheckExistsByUsername_negative() {
        // given
        var nonExistingUsername = "NonExisting.Username";

        // when
        var actualResult = authenticationDao.checkExistsByUsername(nonExistingUsername);

        // then
        assertThat(actualResult).isFalse();
    }


    // ==================== CHECK PASSWORD FOR USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return true if password for given username is correct")
    void testCheckPasswordForUsername_positive() {
        // given
        var existingUsername = "John.Doe";
        var correctPassword = "password123";

        // when
        var actualResult = authenticationDao.checkPasswordForUsername(existingUsername, correctPassword);

        // then
        assertThat(actualResult).isTrue();
    }

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return false if password for given username is incorrect")
    void testCheckPasswordForUsername_negative() {
        // given
        var existingUsername = "John.Doe";
        var incorrectPassword = "wrongPassword";

        // when
        var actualResult = authenticationDao.checkPasswordForUsername(existingUsername, incorrectPassword);

        // then
        assertThat(actualResult).isFalse();
    }


    // ==================== CHANGE PASSWORD FOR USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method changePasswordForUsername - should change password for given username and return true when checkPasswordForUsername is called with new password")
    void testChangePasswordForUsername() {
        // given
        var existingUsername = "John.Doe";
        var newPassword = "newPassword123";

        // when
        authenticationDao.changePasswordForUsername(existingUsername, newPassword);
        var actualResult = authenticationDao.checkPasswordForUsername(existingUsername, newPassword);

        // then
        assertThat(actualResult).isTrue();
    }

}