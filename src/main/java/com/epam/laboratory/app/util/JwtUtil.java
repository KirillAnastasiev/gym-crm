package com.epam.laboratory.app.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
@NoArgsConstructor
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class JwtUtil {

    @Value("${jwt.secret:secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration-seconds:3600}")
    private int accessTokenExpirationSeconds;

    @Value("${jwt.refresh.expiration-seconds:86400}")
    private int refreshTokenExpirationSeconds;

    public String generateAccessToken(String username) {
        checkUsername(username);
        var signingKey = getSigningKey();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + SECONDS.toMillis(accessTokenExpirationSeconds)))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(String username) {
        checkUsername(username);
        var signingKey = getSigningKey();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + SECONDS.toMillis(refreshTokenExpirationSeconds)))
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            var signingKey = getSigningKey();
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .after(new Date(System.currentTimeMillis()));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Optional<String> getUsernameFromToken(String token) {
        try {
            var signingKey = getSigningKey();
            var username = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Optional.ofNullable(username);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private void checkUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
    }

}
