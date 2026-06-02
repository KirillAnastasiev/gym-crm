package com.epam.laboratory.app.security.listener;

import com.epam.laboratory.app.service.security.UserSecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationEventListenerTest {

    @Mock
    private UserSecurityService userSecurityService;

    @InjectMocks
    private AuthenticationEventListener authenticationEventListener;


    // ==================== ON SUCCESS ====================

    @Test
    @DisplayName("Test of the method onSuccess - should reset failed attempts for the user")
    void testOnSuccess() {
        // given
        var successEvent = new AuthenticationSuccessEvent(new UsernamePasswordAuthenticationToken(null, null));

        // when
        authenticationEventListener.onSuccess(successEvent);

        // then
        verify(userSecurityService, times(1)).resetFailedAttempts(anyString());
        verifyNoMoreInteractions(userSecurityService);
    }


    // ==================== ON FAILURE ====================

    @Test
    @DisplayName("Test of the method onFailure - should increase failed attempts for the user")
    void testOnFailure() {
        // given
        var successEvent = new AuthenticationFailureCredentialsExpiredEvent(
                new UsernamePasswordAuthenticationToken(null, null),
                new BadCredentialsException("Bad credentials")
        );

        // when
        authenticationEventListener.onFailure(successEvent);

        // then
        verify(userSecurityService, times(1)).increaseFailedAttempts(anyString());
        verifyNoMoreInteractions(userSecurityService);
    }
}