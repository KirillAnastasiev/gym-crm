package com.kirill.projects.gymcrm.app.security;

import com.kirill.projects.gymcrm.app.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {

    private final JwtService jwtService;

    @Override
    public Authentication convert(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                var tokenObj = jwtService.deserializeToken(token);
                jwtService.validateToken(tokenObj, JwtToken.JwtTokenType.ACCESS);
                return new PreAuthenticatedAuthenticationToken(tokenObj, token);
            } catch (Exception e) {
                throw new BadCredentialsException(e.getMessage());
            }
        }
        throw new AuthenticationCredentialsNotFoundException("Missing or invalid Authorization header");
    }
}
