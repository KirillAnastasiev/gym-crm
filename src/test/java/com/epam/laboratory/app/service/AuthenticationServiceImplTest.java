package com.epam.laboratory.app.service;

import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.repository.AuthenticationDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationDao authenticationDao;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    @DisplayName("Test of the method checkExistsByUsername - should return true when user exists")
    void testCheckExistsByUsername_positive() {
        // given
        String username = "FirstName.LastName";

        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(true);

        // when
        var actualResult = authenticationService.checkExistsByUsername(username);

        // then
        assertThat(actualResult).isTrue();

        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationDao);
    }

    @Test
    @DisplayName("Test of the method checkExistsByUsername - should return false when user does not exist")
    void testCheckExistsByUsername_negative_notExistedUser() {
        // given
        String username = "FirstName.LastName";

        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(false);

        // when
        var actualResult = authenticationService.checkExistsByUsername(username);

        // then
        assertThat(actualResult).isFalse();

        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, 'Username must not be null'",
            "'   ', 'Username must not be blank'"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method checkExistsByUsername - should throw IllegalArgumentException when username is invalid")
    void testCheckExistsByUsername_negative_invalidUsername(String username, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> authenticationService.checkExistsByUsername(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(authenticationDao);
    }

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return true if password is correct for given username")
    void testCheckPasswordForUsername_positive() {
        // given
        var username = "FirstName.LastName";
        var password = "password";

        given(authenticationDao.checkPasswordForUsername(anyString(), anyString())).willReturn(true);

        // when
        var actualResult = authenticationService.checkPasswordForUsername(username, password);

        // then
        assertThat(actualResult).isTrue();

        verify(authenticationDao, times(1)).checkPasswordForUsername(anyString(), anyString());
        verifyNoMoreInteractions(authenticationDao);
    }

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return false if password is incorrect for given username")
    void testCheckPasswordForUsername_negative_notCorrectPassword() {
        // given
        var username = "FirstName.LastName";
        var password = "password";

        given(authenticationDao.checkPasswordForUsername(anyString(), anyString())).willReturn(false);

        // when
        var actualResult = authenticationService.checkPasswordForUsername(username, password);

        // then
        assertThat(actualResult).isFalse();

        verify(authenticationDao, times(1)).checkPasswordForUsername(anyString(), anyString());
        verifyNoMoreInteractions(authenticationDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, password, 'Username and password must not be null'",
            "username, NULL, 'Username and password must not be null'",
            "'   ', password, 'Username and password must not be blank'",
            "username, '   ', 'Username and password must not be blank'"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method checkPasswordForUsername - should throw IllegalArgumentException when username or password is invalid")
    void testCheckPasswordForUsername_negative_invalidInput(String username, String password, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> authenticationService.checkPasswordForUsername(username, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(authenticationDao);
    }

    @Test
    @DisplayName("Test of the method validateUser - should not throw exception when user exists and password is correct")
    void testValidateUser_positive() {
        // given
        var username = "FirstName.LastName";
        var password = "password";

        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(true);
        given(authenticationDao.checkPasswordForUsername(anyString(), anyString())).willReturn(true);

        // when
        authenticationService.validateUser(username, password);

        // then
        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verify(authenticationDao, times(1)).checkPasswordForUsername(anyString(), anyString());
        verifyNoMoreInteractions(authenticationDao);
    }

    @Test
    @DisplayName("Test of the method validateUser - should throw AuthenticationException when user does not exist")
    void testValidateUser_negative_userDoesNotExist() {
        // given
        var username = "FirstName.LastName";
        var password = "password";

        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authenticationService.validateUser(username, password))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("User with username %s does not exist".formatted(username));

        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationDao);
    }

    @Test
    @DisplayName("Test of the method validateUser - should throw AuthenticationException when password is incorrect for given user")
    void testValidateUser_negative_incorrectPassword() {
        // given
        var username = "FirstName.LastName";
        var password = "password";

        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(true);
        given(authenticationDao.checkPasswordForUsername(anyString(), anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authenticationService.validateUser(username, password))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Incorrect password for username %s".formatted(username));

        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verify(authenticationDao, times(1)).checkPasswordForUsername(anyString(), anyString());
        verifyNoMoreInteractions(authenticationDao);
    }

    @Test
    @DisplayName("Test of the method changePassword - should change password successfully when username, old password and new password are valid")
    void testChangePassword_positive() {
        // given
        var username = "FirstName.LastName";
        var oldPassword = "oldPassword";
        var newPassword = "newPassword";

        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(true);
        given(authenticationDao.checkPasswordForUsername(anyString(), anyString())).willReturn(true);
        doNothing().when(authenticationDao).changePasswordForUsername(anyString(), anyString());

        // when
        authenticationService.changePassword(username, oldPassword, newPassword);

        // then
        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verify(authenticationDao, times(1)).checkPasswordForUsername(anyString(), anyString());
        verify(authenticationDao, times(1)).changePasswordForUsername(anyString(), anyString());
        verifyNoMoreInteractions(authenticationDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, oldPassword, newPassword, 'Username, old password and new password must not be null'",
            "username, NULL, newPassword, 'Username, old password and new password must not be null'",
            "username, oldPassword, NULL, 'Username, old password and new password must not be null'",
            "'   ', oldPassword, newPassword, 'Username, old password and new password must not be blank'",
            "username, '   ', newPassword, 'Username, old password and new password must not be blank'",
            "username, oldPassword, '   ', 'Username, old password and new password must not be blank'"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method changePassword - should throw IllegalArgumentException when username, old password or new password is invalid")
    void testChangePassword_negative_invalidInput(String username, String oldPassword, String newPassword, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> authenticationService.changePassword(username, oldPassword, newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(authenticationDao);
    }
}