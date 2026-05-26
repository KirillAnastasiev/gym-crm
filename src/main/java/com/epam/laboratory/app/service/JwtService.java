package com.epam.laboratory.app.service;

import com.epam.laboratory.app.security.JwtToken;

public interface JwtService {
    boolean isEnabled();
    String generateAccessToken(String username);
    String generateRefreshToken(String username);
    void validateToken(String token, JwtToken.JwtTokenType tokenType);
    void revokeTokenIfExists(String username, JwtToken.JwtTokenType tokenType);
    String getUsernameFromToken(String token);
}
