package com.epam.laboratory.app.security;

import com.epam.laboratory.app.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JwtAuthenticationConverter implements AuthenticationConverter {

    private final JwtService jwtService;

    @Override
    public @Nullable Authentication convert(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                var tokenObj = jwtService.deserializeToken(token);
                return new PreAuthenticatedAuthenticationToken(tokenObj, token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
