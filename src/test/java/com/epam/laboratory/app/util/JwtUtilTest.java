package com.epam.laboratory.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtUtil.setSecretKey("mySecretKeyForJWTTokenGenerationAndValidationPurpose123456");
        jwtUtil.setAccessTokenExpirationSeconds(3600);
        jwtUtil.setRefreshTokenExpirationSeconds(86400);
    }

    @Test
    @DisplayName("Test of the method generateAccessToken - should generate non-null string access token")
    void testGenerateAccessToken_positive() {
        // given
        var username = "testUser";

        // when
        var actualResult = jwtUtil.generateAccessToken(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(String.class);
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
        assertThatThrownBy(() -> jwtUtil.generateAccessToken(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }

    @Test
    @DisplayName("Test of the method generateRefreshToken - should generate non-null string refresh token")
    void testGenerateRefreshToken_positive() {
        // given
        var username = "testUser";

        // when
        var actualResult = jwtUtil.generateRefreshToken(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(String.class);
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
        assertThatThrownBy(() -> jwtUtil.generateRefreshToken(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }

    @Test
    @DisplayName("Test of the method validateToken - should return true for valid token")
    void testValidateToken_positive() {
        // given
        var username = "testUser";
        var token = jwtUtil.generateAccessToken(username);

        // when
        var actualResult = jwtUtil.validateToken(token);

        // then
        assertThat(actualResult).isTrue();
    }

    @Test
    @DisplayName("Test of the method validateToken - should return false for invalid token")
    void testValidateToken_negative() {
        // given
        var invalidToken = "invalidToken";

        // when
        var actualResult = jwtUtil.validateToken(invalidToken);

        // then
        assertThat(actualResult).isFalse();
    }

    @Test
    @DisplayName("Test of the method getUsernameFromToken - should return correct username from token")
    void testGetUsernameFromToken_positive() {
        // given
        var username = "testUser";
        var token = jwtUtil.generateAccessToken(username);

        // when
        var actualResult = jwtUtil.getUsernameFromToken(token);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).contains(username);
    }

    @Test
    @DisplayName("Test of the method getUsernameFromToken - should return null for invalid token")
    void testGetUsernameFromToken_negative() {
        // given
        var invalidToken = "invalidToken";

        // when
        var actualResult = jwtUtil.getUsernameFromToken(invalidToken);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

}