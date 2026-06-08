package com.epam.laboratory.app.security;

import com.epam.laboratory.app.service.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationConverter test suite")
class JwtAuthenticationConverterTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    JwtAuthenticationConverter converter;


    // ==================== CONVERT TESTS ====================

    @Test
    @DisplayName("Test of the method convert - should return PreAuthenticatedAuthenticationToken when valid token is provided")
    void testConvert_positive() {
        // given
        var tokenObj = createTestJwtToken();
        var token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlzcyI6InRlc3RJc3N1ZXIiLCJhdWQiOiJ0ZXN0QXVkaWVuY2UiLCJpYXQiOjE2ODg4ODQ4MDAsImV4cCI6MTY4ODg5ODQwMCwianRpIjoiNWYyZDEyYjAtZDE1Mi00M2E5LTg5MzQtYjA3ZDUxZDE3MjkifQ.7s8nl8n9sXo2m1e5u7v8w9x01a2b3c4d5e6f7g8h9i0j";
        var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        when(jwtService.deserializeToken(anyString())).thenReturn(tokenObj);
        doNothing().when(jwtService).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));

        // when
        var actualResult = converter.convert(request);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(PreAuthenticatedAuthenticationToken.class);
        assertThat(actualResult.getPrincipal()).isEqualTo(tokenObj);
        assertThat(actualResult.getCredentials()).isEqualTo(token);

        verify(jwtService, times(1)).deserializeToken(anyString());
        verify(jwtService, times(1)).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    @DisplayName("Test of the method convert - should throw AuthenticationCredentialsNotFoundException when Authorization header is missing")
    void testConvert_negative_noAuthorizationHeader() {
        // given
        var request = new MockHttpServletRequest();

        // when && then
        assertThatThrownBy(() -> converter.convert(request))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("Missing or invalid Authorization header");

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Test of the method convert - should throw BadCredentialsException when JWT token is invalid")
    void testConvert_negative_invalidJwtToken() {
        // given
        var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID());

        given(jwtService.deserializeToken(anyString())).willThrow(new IllegalArgumentException("Invalid JWT token"));

        // when & then
        assertThatThrownBy(() -> converter.convert(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid JWT token");

        verify(jwtService, times(1)).deserializeToken(anyString());
        verifyNoMoreInteractions(jwtService);
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

}