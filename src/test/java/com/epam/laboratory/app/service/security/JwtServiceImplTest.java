package com.epam.laboratory.app.service.security;

import com.epam.laboratory.app.domain.JwtTokenEntity;
import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.repository.JwtTokenDao;
import com.epam.laboratory.app.security.JwtToken;
import com.epam.laboratory.app.security.mapper.JwtTokenMapper;
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

    @Mock
    private JwtTokenMapper tokenMapper;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(config, tokenDao, tokenMapper);
    }


    // ==================== CREATE ACCESS TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method createAccessToken - should return access token when username is valid")
    void testCreateAccessToken_positive() {
        // given
        var token = createTestJwtToken();
        var tokenEntity = createTestJwtTokenEntity();

        given(tokenMapper.toEntity(any(JwtToken.class))).willReturn(tokenEntity);
        given(tokenDao.save(any(JwtTokenEntity.class))).willReturn(tokenEntity);

        // when
        var actualResult = jwtService.createAccessToken("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getPayload().getSub()).isEqualTo("FirstName.LastName");
        assertThat(actualResult.getPayload().getJtt()).isEqualTo(JwtToken.JwtTokenType.ACCESS);

        verify(tokenDao, times(1)).save(any(JwtTokenEntity.class));
        verifyNoMoreInteractions(tokenDao);
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
        assertThatThrownBy(() -> jwtService.createAccessToken(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }

    @Test
    @DisplayName("Test of the method createAccessToken - should throw RuntimeException when tokenDao throws exception")
    void testCreateAccessToken_negative_tokenDaoException() {
        // given
        var tokenEntity = createTestJwtTokenEntity();

        given(tokenMapper.toEntity(any(JwtToken.class))).willReturn(tokenEntity);
        given(tokenDao.save(any(JwtTokenEntity.class))).willThrow(new RuntimeException("Database error"));

        // when & then
        assertThatThrownBy(() -> jwtService.createAccessToken("FirstName.LastName"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(tokenMapper, times(1)).toEntity(any(JwtToken.class));
        verify(tokenDao, times(1)).save(any(JwtTokenEntity.class));
        verifyNoMoreInteractions(tokenDao, tokenMapper);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Username must not be null",
            "'', Username must not be blank",
            "'   ', Username must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method createAccessToken - should throw IllegalArgumentException for invalid input")
    void testCreateAccessToken_negative_invalidInput(String username, String errorMessage) {
        // when & then
        assertThatThrownBy(() -> jwtService.createAccessToken(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }


    // ==================== CREATE REFRESH TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method createRefreshToken - should return refresh token when username is valid")
    void testCreateRefreshToken_positive() {
        // given
        var token = createTestJwtToken();
        token.getPayload().setJtt(JwtToken.JwtTokenType.REFRESH);
        var tokenEntity = createTestJwtTokenEntity();
        tokenEntity.setTokenType("REFRESH");

        given(tokenMapper.toEntity(any(JwtToken.class))).willReturn(tokenEntity);
        given(tokenDao.save(any(JwtTokenEntity.class))).willReturn(tokenEntity);

        // when
        var actualResult = jwtService.createRefreshToken("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getPayload().getSub()).isEqualTo("FirstName.LastName");
        assertThat(actualResult.getPayload().getJtt()).isEqualTo(JwtToken.JwtTokenType.REFRESH);

        verify(tokenMapper, times(1)).toEntity(any(JwtToken.class));
        verify(tokenDao, times(1)).save(any(JwtTokenEntity.class));
        verifyNoMoreInteractions(tokenDao, tokenMapper);
    }

    @Test
    @DisplayName("Test of the method createRefreshToken - should throw RuntimeException when tokenDao throws exception")
    void testCreateRefreshToken_negative_tokenDaoException() {
        // given
        var tokenEntity = createTestJwtTokenEntity();

        given(tokenMapper.toEntity(any(JwtToken.class))).willReturn(tokenEntity);
        given(tokenDao.save(any(JwtTokenEntity.class))).willThrow(new RuntimeException("Database error"));

        // when & then
        assertThatThrownBy(() -> jwtService.createRefreshToken("FirstName.LastName"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(tokenMapper, times(1)).toEntity(any(JwtToken.class));
        verify(tokenDao, times(1)).save(any(JwtTokenEntity.class));
        verifyNoMoreInteractions(tokenDao, tokenMapper);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Username must not be null",
            "'', Username must not be blank",
            "'   ', Username must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method createRefreshToken - should throw IllegalArgumentException for invalid input")
    void testCreateRefreshToken_negative_invalidInput(String username, String errorMessage) {
        // when & then
        assertThatThrownBy(() -> jwtService.createRefreshToken(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
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
        assertThatThrownBy(() -> jwtService.createRefreshToken(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }


    // ==================== SERIALIZE TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method serializeToken - should return serialized token string for valid token")
    void testSerializeToken_positive() {
        // given
        var token = createTestJwtToken();

        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.toJwtString(any(JwtToken.class))).thenReturn(ACCESS_TOKEN);

            // when
            var actualResult = jwtService.serializeToken(token);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult).isEqualTo(ACCESS_TOKEN);
        }
    }

    @Test
    @DisplayName("Test of the method serializeToken - should throw IllegalArgumentException for null token")
    void testSerializeToken_negative_invalidInput() {
        // when & then
        assertThatThrownBy(() -> jwtService.serializeToken(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token must not be null");
    }


    // ==================== DESERIALIZE TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method deserializeToken - should return JwtToken object for valid token string")
    void testDeserializeToken_positive() {
        // given
        var token = createTestJwtToken();

        try (var mockedStatic = mockStatic(JwtFactoryUtil.class)) {
            mockedStatic.when(() -> JwtFactoryUtil.fromJwtString(anyString(), anyString())).thenReturn(token);

            // when
            var actualResult = jwtService.deserializeToken(ACCESS_TOKEN);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getPayload().getSub()).isEqualTo("FirstName.LastName");
            assertThat(actualResult.getPayload().getJtt()).isEqualTo(JwtToken.JwtTokenType.ACCESS);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Token must not be null",
            "'', Token must not be blank",
            "'   ', Token must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method deserializeToken - should throw IllegalArgumentException for invalid input")
    void testDeserializeToken_negative_invalidInput(String tokenString, String errorMessage) {
        // when & then
        assertThatThrownBy(() -> jwtService.deserializeToken(tokenString))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }


    // ==================== VALIDATE TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method validateToken - should not throw exception for valid token")
    void testValidateToken_positive() {
        // given
        var token = createTestJwtToken();
        var tokenEntity = createTestJwtTokenEntity();

        given(tokenMapper.toEntity(any(JwtToken.class))).willReturn(tokenEntity);
        given(tokenDao.isRevokedById(any(UUID.class))).willReturn(false);

        // when & then
        assertThatNoException().isThrownBy(() -> jwtService.validateToken(token, JwtToken.JwtTokenType.ACCESS));

        verify(tokenMapper, times(1)).toEntity(any(JwtToken.class));
        verify(tokenDao, times(1)).isRevokedById(any(UUID.class));
        verifyNoMoreInteractions(tokenMapper, tokenDao);
    }

    @Test
    @DisplayName("Test of the method validateToken - should throw IllegalArgumentException for invalid token")
    void testValidateToken_negative_invalidToken() {
        // given
        var token = createTestJwtToken();
        token.getPayload().sub("");

        // when & then
        assertThatThrownBy(() -> jwtService.validateToken(token, JwtToken.JwtTokenType.ACCESS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid JWT token");

        verifyNoInteractions(tokenDao);
    }

    @Test
    @DisplayName("Test of the method validateToken - should throw IllegalArgumentException for revoked token")
    void testValidateToken_negative_revokedToken() {
        // given
        var token = createTestJwtToken();
        var tokenEntity = createTestJwtTokenEntity();

        given(tokenMapper.toEntity(any(JwtToken.class))).willReturn(tokenEntity);
        given(tokenDao.isRevokedById(any(UUID.class))).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jwtService.validateToken(token, JwtToken.JwtTokenType.ACCESS))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("JWT token is revoked");

        verify(tokenMapper, times(1)).toEntity(any(JwtToken.class));
        verify(tokenDao, times(1)).isRevokedById(any(UUID.class));
        verifyNoMoreInteractions(tokenDao);
    }

    @Test
    @DisplayName("Test of the method validateToken - should throw IllegalArgumentException for token with invalid type")
    void testValidateToken_negative_notValidTokenType() {
        // given
        var token = createTestJwtToken();

        // when
        assertThatThrownBy(() -> jwtService.validateToken(token, JwtToken.JwtTokenType.REFRESH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid JWT token");

        verifyNoInteractions(tokenDao);
    }


    // ==================== REVOKE TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method revokeTokenIfExists - should revoke existing token for the user and type")
    void testRevokeToken_positive() {
        // given
        var token = createTestJwtToken();
        var tokenEntity = createTestJwtTokenEntity();

        given(tokenDao.findTopByUsernameAndTokenTypeAndIsRevokedFalseOrderByIdDesc(anyString(), anyString()))
                .willReturn(Optional.of(tokenEntity));
        doNothing().when(tokenDao).revokeByUsername(anyString());

        // when
        jwtService.revokeToken("FirstName.LastName", JwtToken.JwtTokenType.ACCESS);

        // then
        verify(tokenDao, times(1)).findTopByUsernameAndTokenTypeAndIsRevokedFalseOrderByIdDesc(anyString(), anyString());
        verify(tokenDao, times(1)).revokeByUsername(anyString());
        verifyNoMoreInteractions(tokenDao);
    }

    @Test
    @DisplayName("Test of the method revokeTokenIfExists - should do nothing if no token exists for the user and type")
    void testRevokeToken_negative_notExistingToken() {
        // given
        var token = createTestJwtToken();
        var tokenEntity = createTestJwtTokenEntity();

        given(tokenDao.findTopByUsernameAndTokenTypeAndIsRevokedFalseOrderByIdDesc(anyString(), anyString()))
                .willReturn(Optional.of(tokenEntity));

        // when
        jwtService.revokeToken("FirstName.LastName", JwtToken.JwtTokenType.ACCESS);

        // then
        verify(tokenDao, times(1)).findTopByUsernameAndTokenTypeAndIsRevokedFalseOrderByIdDesc(anyString(), anyString());
        verify(tokenDao, times(1)).revokeByUsername(anyString());
        verifyNoMoreInteractions(tokenDao);
    }

    @Test
    @DisplayName("Test of the method revokeTokenIfExists - should throw RuntimeException when tokenDao throws exception")
    void testRevokeToken_negative_daoException() {
        // given
        var token = createTestJwtToken();

        given(tokenDao.findTopByUsernameAndTokenTypeAndIsRevokedFalseOrderByIdDesc(anyString(), anyString()))
                .willThrow(new RuntimeException("Database error"));

        // when & then
        assertThatThrownBy(() -> jwtService.revokeToken("FirstName.LastName", JwtToken.JwtTokenType.ACCESS))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(tokenDao, times(1)).findTopByUsernameAndTokenTypeAndIsRevokedFalseOrderByIdDesc(anyString(), anyString());
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
        assertThatThrownBy(() -> jwtService.revokeToken(username, JwtToken.JwtTokenType.ACCESS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }


    // ==================== GET USERNAME FROM TOKEN TESTS ====================

    @Test
    @DisplayName("Test of the method getUsernameFromToken - should return username for valid token")
    void testGetUsernameFromToken_positive() {
        // given
        var jwtToken = createTestJwtToken();

        // when
        var actualResult = jwtService.getUsernameFromToken(jwtToken);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo("FirstName.LastName");
    }

    @Test
    @DisplayName("Test of the method getUsernameFromToken - should throw IllegalArgumentException for null token")
    void testGetUsernameFromToken_negative_nullToken() {
        // when & then
        assertThatThrownBy(() -> jwtService.getUsernameFromToken(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token must not be null");

    }

    private static JwtToken createTestJwtToken() {
        var id = UUID.randomUUID();
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
                        .jti(id))
                .secretKey("mySecretKeyForJWTTokenGenerationAndValidationPurpose123456");
    }

    private static JwtTokenEntity createTestJwtTokenEntity() {
        var token = new JwtTokenEntity();
        token.setId(UUID.randomUUID());
        token.setTokenType("ACCESS");
        token.setUsername("FirstName.LastName");
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        token.setIsRevoked(false);
        return token;
    }

}