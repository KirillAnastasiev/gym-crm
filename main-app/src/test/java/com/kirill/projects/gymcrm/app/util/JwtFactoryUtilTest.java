package com.kirill.projects.gymcrm.app.util;

import com.kirill.projects.gymcrm.app.security.JwtToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("JwtFactoryUtil test suite")
class JwtFactoryUtilTest {
    private static final String SECRET_KEY = "mySecretKeyForJWTTokenGenerationAndValidationPurpose123456";

    @Test
    @DisplayName("Test of the method toJwtString - should return JWT string when JwtToken object is valid")
    void testToJwtString_positive() {
        // given
        var jwtToken = getTestJwtToken();

        // when
        var jwtString = JwtFactoryUtil.toJwtString(jwtToken);

        // then
        assertThat(jwtString).isNotNull();
    }

    @Test
    @DisplayName("Test of the method fromJwtString - should return JwtToken object when JWT string is valid")
    void testFromJwtString_positive() {
        // given
        var jwtToken = getTestJwtToken();
        var jwtString = JwtFactoryUtil.toJwtString(jwtToken);

        // when
        var actualResult = JwtFactoryUtil.fromJwtString(jwtString, jwtToken.getSecretKey());

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getHeader()).isEqualTo(jwtToken.getHeader());
        assertThat(actualResult.getPayload()).isEqualTo(jwtToken.getPayload());
    }

    @Test
    @DisplayName("Test of the method fromJwtString - should throw IllegalArgumentException when JWT string is invalid")
    void testFromJwtString_negative() {
        // given
        var invalidJwtString = "invalid.jwt.string";

        // when
        assertThatThrownBy(() -> JwtFactoryUtil.fromJwtString(invalidJwtString, SECRET_KEY))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid JWT token");
    }

    private static JwtToken getTestJwtToken() {
        return new JwtToken()
                .header(new JwtToken.Header()
                        .typ("JWT")
                        .alg("HS256"))
                .payload(new JwtToken.Payload()
                        .jti(UUID.randomUUID())
                        .jtt(JwtToken.JwtTokenType.ACCESS)
                        .sub("FirstName.LastName")
                        .iss("testIssuer")
                        .aud("testAudience")
                        .iat(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                        .exp(Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.SECONDS)))
                .secretKey("mySecretKeyForJWTTokenGenerationAndValidationPurpose123456");
    }
}