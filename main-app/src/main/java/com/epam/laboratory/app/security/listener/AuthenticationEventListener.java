package com.epam.laboratory.app.security.listener;

import com.epam.laboratory.app.service.security.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationEventListener {

    private final UserSecurityService userSecurityService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        var username = successEvent.getAuthentication().getName();
        userSecurityService.resetFailedAttempts(username);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        var username = failureEvent.getAuthentication().getName();
        userSecurityService.increaseFailedAttempts(username);
    }

}
