package com.epam.laboratory.app.service;

import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.repository.JwtTokenDao;
import com.epam.laboratory.app.security.JwtToken;
import com.epam.laboratory.app.util.JwtFactoryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtServiceImpl test suite")

class JwtServiceImplTest {
    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlzcyI6InRlc3RJc3N1ZXIiLCJhdWQiOiJ0ZXN0QXVkaWVuY2UiLCJpYXQiOjE2ODg4ODQ4MDAsImV4cCI6MTY4ODg5ODQwMCwianRpIjoiNWYyZDEyYjAtZDE1Mi00M2E5LTg5MzQtYjA3ZDUxZDE3MjkifQ.7s8nl8n9sXo2m1e5u7v8w9x01a2b3c4d5e6f7g8h9i0j";
    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlzcyI6InRlc3RJc3N1ZXIiLCJhdWQiOiJ0ZXN0QXVkaWVuY2UiLCJpYXQiOjE2ODg4ODQ4MDAsImV4cCI6MTY4ODg5ODQwMCwianRpIjoiNWYyZDEyYjAtZDE1Mi00M2E5LTg5MzQtYjA3ZDUxZDE3MjkifQ.7s8nl8n9sXo2m1e5u7v8w9x01a2b3c4d5e6f7g8h9i0j";

    private final JwtServiceImpl.JwtConfiguration config = new JwtServiceImpl.JwtConfiguration(
            true,
            "HS256",
            "testIssuer",
            "testAudience",
            3600,
            86400,
            "mySecretKeyForJWTTokenGenerationAndValidationPurpose123456"
    );

    @Mock
    private JwtTokenDao tokenDao;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(tokenDao, config);
    }

    // ==================== GENERATE ACCESS TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method generateAccessToken - should return access token when username is valid")
    void testGenerateAccessToken_positive() {
        // given
        var token = getTestJwtToken();

        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.toJwtString(any(JwtToken.class))).thenReturn(ACCESS_TOKEN);
            given(tokenDao.findLastNotRevokedByUsernameAndType(anyString(), any(JwtToken.JwtTokenType.class)))
                    .willReturn(Optional.empty());
            given(tokenDao.save(any(JwtToken.class))).willReturn(token);

            // when
            var actualResult = jwtService.generateAccessToken("FirstName.LastName");

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult).isEqualTo(ACCESS_TOKEN);

            verify(tokenDao, times(1)).findLastNotRevokedByUsernameAndType(anyString(), any(JwtToken.JwtTokenType.class));
            verify(tokenDao, times(1)).save(any(JwtToken.class));
            verifyNoMoreInteractions(tokenDao);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Username must not be null",
            "'', Username must not be blank",
            "'   ', Username must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method generateAccessToken - should throw IllegalArgumentException for invalid input")
    void testGenerateAccessToken_negative_invalidInput(String username, String errorMessage) {
        // when & then
        assertThatThrownBy(() -> jwtService.generateAccessToken(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }

    // ==================== GENERATE REFRESH TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method generateRefreshToken - should return refresh token when username is valid")
    void testGenerateRefreshToken_positive() {
        // given
        var token = getTestJwtToken();

        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.toJwtString(any(JwtToken.class))).thenReturn(REFRESH_TOKEN);
            given(tokenDao.findLastNotRevokedByUsernameAndType(anyString(), any(JwtToken.JwtTokenType.class)))
                    .willReturn(Optional.empty());
            given(tokenDao.save(any(JwtToken.class))).willReturn(token);

            // when
            var actualResult = jwtService.generateRefreshToken("FirstName.LastName");

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult).isEqualTo(REFRESH_TOKEN);

            verify(tokenDao, times(1)).findLastNotRevokedByUsernameAndType("FirstName.LastName", JwtToken.JwtTokenType.REFRESH);
            verify(tokenDao, times(1)).save(any(JwtToken.class));

            verifyNoMoreInteractions(tokenDao);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Username must not be null",
            "'', Username must not be blank",
            "'   ', Username must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method generateRefreshToken - should throw IllegalArgumentException for invalid input")
    void testGenerateRefreshToken_negative_invalidInput(String username, String errorMessage) {
        // when & then
        assertThatThrownBy(() -> jwtService.generateRefreshToken(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }


    // ==================== VALIDATE TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method validateToken - should not throw exception for valid token")
    void testValidateToken_positive() {
        // given
        var token = getTestJwtToken();
        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.fromJwtString(anyString(), anyString())).thenReturn(token);
            given(tokenDao.isRevokedById(any(UUID.class))).willReturn(false);

            // when & then
            assertThatNoException().isThrownBy(() -> jwtService.validateToken(ACCESS_TOKEN, JwtToken.JwtTokenType.ACCESS));

            verify(tokenDao, times(1)).isRevokedById(any(UUID.class));
            verifyNoMoreInteractions(tokenDao);
        }
    }

    @Test
    @DisplayName("Test of the method validateToken - should throw IllegalArgumentException for token with invalid type")
    void testValidateToken_negative_invalidTokenType() {
        // given
        var token = getTestJwtToken();
        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.fromJwtString(anyString(), anyString())).thenReturn(token);

            // when & then
            assertThatThrownBy(() -> jwtService.validateToken(ACCESS_TOKEN, JwtToken.JwtTokenType.REFRESH))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid JWT token type");

            verifyNoInteractions(tokenDao);
        }
    }

    @Test
    @DisplayName("Test of the method validateToken - should throw IllegalArgumentException for invalid token")
    void testValidateToken_negative_invalidToken() {
        // given
        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.fromJwtString(anyString(), anyString())).thenThrow(new IllegalArgumentException("Invalid JWT token"));

            // when & then
            assertThatThrownBy(() -> jwtService.validateToken(ACCESS_TOKEN, JwtToken.JwtTokenType.ACCESS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid JWT token");

            verifyNoInteractions(tokenDao);
        }
    }

    @Test
    @DisplayName("Test of the method validateToken - should throw IllegalArgumentException for revoked token")
    void testValidateToken_negative_revokedToken() {
        // given
        var token = getTestJwtToken();
        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.fromJwtString(anyString(), anyString())).thenReturn(token);
            given(tokenDao.isRevokedById(any(UUID.class))).willReturn(true);

            // when & then
            assertThatThrownBy(() -> jwtService.validateToken(ACCESS_TOKEN, JwtToken.JwtTokenType.ACCESS))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("JWT token is revoked");

            verify(tokenDao, times(1)).isRevokedById(any(UUID.class));
            verifyNoMoreInteractions(tokenDao);
        }
    }


    // ==================== REVOKE TOKEN IF EXISTS TESTS ====================

    @Test
    @DisplayName("Test of the method revokeTokenIfExists - should revoke existing token for the user and type")
    void testRevokeTokenIfExists_positive() {
        // given
        var token = getTestJwtToken();
        given(tokenDao.findLastNotRevokedByUsernameAndType(anyString(), any(JwtToken.JwtTokenType.class)))
                .willReturn(Optional.of(token));

        // when
        jwtService.revokeTokenIfExists("FirstName.LastName", JwtToken.JwtTokenType.ACCESS);

        // then
        verify(tokenDao, times(1)).findLastNotRevokedByUsernameAndType(anyString(), any(JwtToken.JwtTokenType.class));
        verify(tokenDao, times(1)).revoke(token.getId());
        verifyNoMoreInteractions(tokenDao);
    }

    @Test
    @DisplayName("Test of the method revokeTokenIfExists - should do nothing if no token exists for the user and type")
    void testRevokeTokenIfExists_negative_notExistingToken() {
        // given
        var token = getTestJwtToken();
        given(tokenDao.findLastNotRevokedByUsernameAndType(anyString(), any(JwtToken.JwtTokenType.class)))
                .willReturn(Optional.of(token));

        // when
        jwtService.revokeTokenIfExists("FirstName.LastName", JwtToken.JwtTokenType.ACCESS);

        // then
        verify(tokenDao, times(1)).findLastNotRevokedByUsernameAndType(anyString(), any(JwtToken.JwtTokenType.class));
        verify(tokenDao, times(1)).revoke(token.getId());
        verifyNoMoreInteractions(tokenDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Username must not be null",
            "'', Username must not be blank",
            "'   ', Username must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method revokeTokenIfExists - should throw IllegalArgumentException for invalid input")
    void testRevokeTokenIfExists_negative_invalidInput(String username, String errorMessage) {
        // when & then
        assertThatThrownBy(() -> jwtService.revokeTokenIfExists(username, JwtToken.JwtTokenType.ACCESS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }


    // ==================== GET USERNAME FROM TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method getUsernameFromToken - should return username for valid token")
    void testGetUsernameFromToken_positive() {
        // given
        var jwtToken = getTestJwtToken();

        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.fromJwtString(anyString(), anyString())).thenReturn(jwtToken);

            // when
            var actualResult = jwtService.getUsernameFromToken(ACCESS_TOKEN);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult).isEqualTo("FirstName.LastName");
        }
    }

    @Test
    @DisplayName("Test of the method getUsernameFromToken - should throw IllegalArgumentException for token with blank username")
    void testGetUsernameFromToken_negative_blankUsername() {
        // given
        var jwtToken = getTestJwtToken();
        jwtToken.getPayload().setSub("");

        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.fromJwtString(anyString(), anyString())).thenReturn(jwtToken);

            // when & then
            assertThatThrownBy(() -> jwtService.getUsernameFromToken(ACCESS_TOKEN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid JWT token");
        }
    }

    private static JwtToken getTestJwtToken() {
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

}