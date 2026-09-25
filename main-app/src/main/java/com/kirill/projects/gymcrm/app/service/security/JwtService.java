package com.kirill.projects.gymcrm.app.service.security;

import com.kirill.projects.gymcrm.app.security.JwtToken;

public interface JwtService {
    boolean isEnabled();
    JwtToken createAccessToken(String username);
    JwtToken createRefreshToken(String username);
    String serializeToken(JwtToken token);
    JwtToken deserializeToken(String token);
    void validateToken(JwtToken token, JwtToken.JwtTokenType expectedType);
    void revokeToken(String username, JwtToken.JwtTokenType tokenType);
    void revokeTokens(String username);
    String getUsernameFromToken(JwtToken token);
    boolean isTokenExpired(JwtToken token);
    boolean isTokenRevoked(JwtToken token);
}
