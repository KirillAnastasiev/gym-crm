package com.kirill.projects.gymcrm.app.security.filter;

import com.kirill.projects.gymcrm.app.security.JwtToken;
import com.kirill.projects.gymcrm.app.service.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter test suite")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    // ==================== DO FILTER INTERNAL TESTS ====================

    @Test
    @DisplayName("Test of the method doFilterInternal - should authenticate the user and set the authentication in the security context")
    void testDoFilterInternal_positive() {
        // given
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();
        var principal = new PreAuthenticatedAuthenticationToken(new JwtToken(), null);
        var tokenObj = new JwtToken();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.token.here");

        given(jwtService.isEnabled()).willReturn(true);
        given(jwtService.deserializeToken(anyString())).willReturn(tokenObj);
        doNothing().when(jwtService).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));
        given(authenticationManager.authenticate(any(Authentication.class))).willReturn(principal);

        // when & then
        assertThatNoException().isThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));

        verify(jwtService, times(1)).isEnabled();
        verify(jwtService, times(1)).deserializeToken(anyString());
        verify(jwtService, times(1)).validateToken(any(JwtToken.class), any(JwtToken.JwtTokenType.class));
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verifyNoMoreInteractions(jwtService, authenticationManager);
    }

    @Test
    @DisplayName("Test of the method doFilterInternal - should throw AuthenticationCredentialsNotFoundException when the Authorization header is missing or invalid")
    void testDoFilterInternal_negative_noAuthorizationHeader() {
        // given
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        given(jwtService.isEnabled()).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessageContaining("Missing or invalid Authorization header");

        verify(jwtService, times(1)).isEnabled();
        verifyNoMoreInteractions(jwtService, authenticationManager);
    }

    @Test
    @DisplayName("Test of the method doFilterInternal - should throw BadCredentialsException when the JWT token is invalid")
    void testDoFilterInternal_negative_invalidJwtToken() {
        // given
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here");

        given(jwtService.isEnabled()).willReturn(true);
        given(jwtService.deserializeToken(anyString())).willThrow(new IllegalArgumentException("Invalid JWT token"));

        // when & then
        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid JWT token");

        verify(jwtService, times(1)).isEnabled();
        verify(jwtService, times(1)).deserializeToken(anyString());
        verifyNoMoreInteractions(jwtService);
    }
}