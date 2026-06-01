package com.epam.laboratory.app.service.security;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.User;
import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.AuthenticationDao;
import com.epam.laboratory.app.security.JwtToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationServiceImpl test suite")
class AuthenticationServiceImplTest {
    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJKb2huLkRvZSIsImlhdCI6MTc3ODQzNDQzMywiZXhwIjoxNzc4NDM4MDMzfQ._nbc9r7qbrKpSEw5C3x9Awrj6XejJPj93flN7qlN7Vf3nnnIjpfhPzzDWF0N6SUD";
    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJKb2huLkRvZSIsImlhdCI6MTc3ODQzNDQzMywiZXhwIjoxNzc5NzMwNDMzfQ.CATJEnKWL0Oze6-lcRiU2Ba-Gxl3jDQ80qFSbiOwmWYTgPTU9G8Foa31iKlJgqMX";

    @Mock
    private AuthenticationDao authenticationDao;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;


    // ==================== CHECK EXISTS BY USERNAME TESTS ====================

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


    // ==================== GET USER TOKENS TESTS ====================

    @Test
    @DisplayName("Test of the method getUserTokens - should return access token and refresh token when username is valid")
    void testGetUserTokens_positive() {
        // given
        var accessToken = createTestJwtToken();
        var refreshToken = createTestJwtToken();
        refreshToken.getPayload().jtt(JwtToken.JwtTokenType.REFRESH);
        var user = createTestUser();

        doNothing().when(jwtService).revokeTokens(anyString());
        given(jwtService.createAccessToken(anyString())).willReturn(accessToken);
        given(jwtService.createRefreshToken(anyString())).willReturn(refreshToken);
        given(jwtService.serializeToken(accessToken)).willReturn(ACCESS_TOKEN);
        given(jwtService.serializeToken(refreshToken)).willReturn(REFRESH_TOKEN);

        // when
        var actualResult = authenticationService.getUserTokens("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Map.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).containsEntry("accessToken", ACCESS_TOKEN);
        assertThat(actualResult).containsEntry("refreshToken", REFRESH_TOKEN);

        verify(jwtService, times(1)).revokeTokens(anyString());
        verify(jwtService, times(1)).createAccessToken(anyString());
        verify(jwtService, times(1)).createRefreshToken(anyString());
        verify(jwtService, times(2)).serializeToken(any(JwtToken.class));
        verifyNoMoreInteractions(jwtService);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, 'Username must not be null'",
            "'   ', 'Username must not be blank'"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method getUserTokens - should throw IllegalArgumentException when username is invalid")
    void testGetUserTokens_negative_invalidInput(String username, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> authenticationService.getUserTokens(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(authenticationDao);
    }


    // ==================== REFRESH ACCESS TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method refreshAccessToken - should return new access token when refresh token is valid")
    void testRefreshAccessToken_positive() {
        // given
        var username = "FirstName.LastName";
        var accessToken = createTestJwtToken();
        var refreshToken = createTestJwtToken();
        refreshToken.getPayload().jtt(JwtToken.JwtTokenType.REFRESH);

        given(jwtService.deserializeToken(anyString())).willReturn(refreshToken);
        doNothing().when(jwtService).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));
        doNothing().when(jwtService).revokeTokens(anyString());
        given(jwtService.getUsernameFromToken(any(JwtToken.class))).willReturn(username);
        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(true);
        given(jwtService.createAccessToken(anyString())).willReturn(accessToken);
        given(jwtService.createRefreshToken(anyString())).willReturn(refreshToken);
        given(jwtService.serializeToken(accessToken)).willReturn(ACCESS_TOKEN);
        given(jwtService.serializeToken(refreshToken)).willReturn(REFRESH_TOKEN);

        // when
        var actualResult = authenticationService.refreshAccessToken(REFRESH_TOKEN);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Map.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).containsEntry("accessToken", ACCESS_TOKEN);
        assertThat(actualResult).containsEntry("refreshToken", REFRESH_TOKEN);

        verify(jwtService, times(1)).deserializeToken(anyString());
        verify(jwtService, times(1)).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));
        verify(jwtService, times(1)).revokeTokens(anyString());
        verify(jwtService, times(1)).getUsernameFromToken(any(JwtToken.class));
        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verify(jwtService, times(1)).createAccessToken(anyString());
        verify(jwtService, times(1)).createRefreshToken(anyString());
        verify(jwtService, times(2)).serializeToken(any(JwtToken.class));
        verifyNoMoreInteractions(jwtService, authenticationDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, 'Refresh token must not be null'",
            "'   ', 'Refresh token must not be blank'"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method refreshAccessToken - should throw IllegalArgumentException when refresh token is null or blank")
    void testRefreshAccessToken_negative_invalidInput(String refreshToken, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> authenticationService.refreshAccessToken(refreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(authenticationDao);
    }

    @Test
    @DisplayName("Test of the method refreshAccessToken - should throw AuthenticationException when refresh token is invalid")
    void testRefreshAccessToken_negative_invalidRefreshToken() {
        // given
        var refreshToken = createTestJwtToken();

        given(jwtService.deserializeToken(anyString())).willReturn(refreshToken);
        doThrow(new AuthenticationException("Invalid JWT token")).when(jwtService).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));

        // when & then
        assertThatThrownBy(() -> authenticationService.refreshAccessToken(REFRESH_TOKEN))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid JWT token");

        verify(jwtService, times(1)).deserializeToken(anyString());
        verify(jwtService, times(1)).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    @DisplayName("Test of the method refreshAccessToken - should throw AuthenticationException when username is not found in refresh token")
    void testRefreshAccessToken_negative_usernameNotFoundInRefreshToken() {
        // given
        var refreshToken = createTestJwtToken();
        refreshToken.getPayload().jtt(JwtToken.JwtTokenType.REFRESH);

        given(jwtService.deserializeToken(anyString())).willReturn(refreshToken);
        doNothing().when(jwtService).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));
        given(jwtService.getUsernameFromToken(any(JwtToken.class))).willThrow(new IllegalArgumentException("Invalid JWT token"));

        // when & then
        assertThatThrownBy(() -> authenticationService.refreshAccessToken(REFRESH_TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid JWT token");

        verify(jwtService, times(1)).deserializeToken(anyString());
        verify(jwtService, times(1)).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));
        verify(jwtService, times(1)).getUsernameFromToken(any(JwtToken.class));
        verifyNoMoreInteractions(jwtService);
    }


    // ==================== CHANGE PASSWORD TESTS ====================

    @Test
    @DisplayName("Test of the method changePassword - should change password successfully when username, old password and new password are valid")
    void testChangePassword_positive() {
        // given
        var username = "FirstName.LastName";
        var oldPassword = "oldPassword";
        var newPassword = "newPassword";
        var encodedPassword = "encodedPassword";
        var user = createTestUser();

        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(true);
        given(authenticationDao.findUserByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
        doNothing().when(authenticationDao).changePasswordForUsername(anyString(), anyString());

        // when
        authenticationService.changePassword(username, oldPassword, newPassword);

        // then
        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verify(authenticationDao, times(1)).findUserByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(authenticationDao, times(1)).changePasswordForUsername(anyString(), anyString());
        verifyNoMoreInteractions(authenticationDao, passwordEncoder);
    }

    @Test
    @DisplayName("Test of the method changePassword - should throw IllegalArgumentException when user does not exist")
    void testChangePassword_negative_notExistedUser() {
        // given
        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(false);

        // when && then
        assertThatThrownBy(() -> authenticationService.changePassword("FirstName.LastName", "oldPassword", "newPassword"))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessage("User with username FirstName.LastName does not exist");

        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationDao);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Test of the method changePassword - should throw AuthenticationException when old password is not correct")
    void testChangePassword_negative_notMatchedPassword() {
        // given
        var user = createTestUser();

        given(authenticationDao.checkExistsByUsername(anyString())).willReturn(true);
        given(authenticationDao.findUserByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when && then
        assertThatThrownBy(() -> authenticationService.changePassword("FirstName.LastName", "wrong)Password", "newPassword"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Incorrect password for username FirstName.LastName");

        verify(authenticationDao, times(1)).checkExistsByUsername(anyString());
        verify(authenticationDao, times(1)).findUserByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verifyNoMoreInteractions(authenticationDao, passwordEncoder);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, oldPassword, newPassword, 'Username must not be null'",
            "username, NULL, newPassword, 'Password must not be null'",
            "username, oldPassword, NULL, 'New password must not be null'",
            "'   ', oldPassword, newPassword, 'Username must not be blank'",
            "username, '   ', newPassword, 'Password must not be blank'",
            "username, oldPassword, '   ', 'New password must not be blank'"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method changePassword - should throw IllegalArgumentException when username, old password or new password is invalid")
    void testChangePassword_negative_invalidInput(String username, String oldPassword, String newPassword, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> authenticationService.changePassword(username, oldPassword, newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(authenticationDao);
    }

    private static JwtToken createTestJwtToken() {
        return new JwtToken()
                .header(new JwtToken.Header()
                        .typ("JWT")
                        .alg("HS256"))
                .payload(new JwtToken.Payload()
                        .jtt(JwtToken.JwtTokenType.ACCESS)
                        .sub("FirstName.LastName")
                        .iss("testIssuer")
                        .aud("testAudience")
                        .iat(Instant.now())
                        .exp(Instant.now().plusSeconds(3600))
                        .jti(UUID.randomUUID()))
                .secretKey("mySecretKeyForJWTTokenGenerationAndValidationPurpose123456");
    }

    private static User createTestUser() {
        var user = new Trainee();
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setUsername("FirstName.LastName");
        user.setPassword("encodedOldPassword");
        user.setActive(true);
        return user;
    }

}