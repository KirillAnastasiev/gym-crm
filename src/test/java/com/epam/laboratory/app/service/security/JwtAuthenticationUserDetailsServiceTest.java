package com.epam.laboratory.app.service.security;

import com.epam.laboratory.app.security.JwtToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationUserDetailsService test suite")
class JwtAuthenticationUserDetailsServiceTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationUserDetailsService service;


    // ==================== LOAD USER DETAILS TESTS ====================

    @Test
    @DisplayName("Test of the method loadUserDetails - should return UserDetails when token is valid")
    void testLoadUserDetails_positive() {
        // given
        var token = new JwtToken().payload(new JwtToken.Payload().sub("John.Doe").jtt(JwtToken.JwtTokenType.ACCESS));
        var tokenPrincipal = new PreAuthenticatedAuthenticationToken(token, "JwtToken");

        given(jwtService.getUsernameFromToken(any(JwtToken.class))).willReturn("John.Doe");
        given(jwtService.isTokenExpired(any(JwtToken.class))).willReturn(false);
        given(jwtService.isTokenRevoked(any(JwtToken.class))).willReturn(false);

        // when
        var actualResult = service.loadUserDetails(tokenPrincipal);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(UserDetails.class);
        assertThat(actualResult.getUsername()).isEqualTo("John.Doe");
        assertThat(actualResult.getAuthorities()).hasSize(1);
        actualResult.getAuthorities().forEach(authority ->
                assertThat(authority.getAuthority()).isEqualTo("ACCESS"));

        verify(jwtService, times(1)).getUsernameFromToken(any(JwtToken.class));
        verify(jwtService, times(1)).isTokenExpired(any(JwtToken.class));
        verify(jwtService, times(1)).isTokenRevoked(any(JwtToken.class));
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    @DisplayName("Test of the method loadUserDetails - should throw UsernameNotFoundException when token is invalid")
    void testLoadUserDetails_negative_notValidTokenPrincipal() {
        // given
        var tokenPrincipal = new PreAuthenticatedAuthenticationToken("invalidPrincipal", "invalidCredentials");

        // when & then
        assertThatThrownBy(() -> service.loadUserDetails(tokenPrincipal))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid authentication token");

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Test of the method loadUserDetails - should throw UsernameNotFoundException when exception occurs during token validation")
    void testLoadUserDetails_negative_exceptionWhileTokenValidation() {
        // given
        var token = new JwtToken().payload(new JwtToken.Payload().sub("John.Doe").jtt(JwtToken.JwtTokenType.ACCESS));
        var tokenPrincipal = new PreAuthenticatedAuthenticationToken(token, "JwtToken");

        given(jwtService.getUsernameFromToken(any(JwtToken.class))).willReturn("John.Doe");
        given(jwtService.isTokenExpired(any(JwtToken.class))).willReturn(false);
        given(jwtService.isTokenRevoked(any(JwtToken.class))).willThrow(new IllegalArgumentException("Token validation error"));

        // when & then
        assertThatThrownBy(() -> service.loadUserDetails(tokenPrincipal))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Token validation error");

        verify(jwtService, times(1)).getUsernameFromToken(any(JwtToken.class));
        verify(jwtService, times(1)).isTokenExpired(any(JwtToken.class));
        verify(jwtService, times(1)).isTokenRevoked(any(JwtToken.class));
        verifyNoMoreInteractions(jwtService);
    }
}