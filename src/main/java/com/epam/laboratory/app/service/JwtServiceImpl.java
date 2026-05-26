package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.repository.JwtTokenDao;
import com.epam.laboratory.app.security.JwtToken;
import com.epam.laboratory.app.util.InputDataValidator;
import com.epam.laboratory.app.util.JwtFactoryUtil;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Getter
@Setter(AccessLevel.PACKAGE)
public class JwtServiceImpl implements JwtService {
    private static final String JWT_TOKEN_TYPE = "JWT";

    private static final Function<JwtToken, String> ENCODER = JwtFactoryUtil::toJwtString;
    private static final BiFunction<String, String, JwtToken> DECODER = JwtFactoryUtil::fromJwtString;

    private final JwtTokenDao tokenDao;
    private final JwtConfiguration config;

    @PostConstruct
    private void validateSecret() {
        if (!isEnabled()) return;
        checkSecretString();
    }

    @Logging(Level.INFO)
    @Override
    public boolean isEnabled() {
        return config.enabled();
    }

    @Logging(Level.INFO)
    @Override
    public String generateAccessToken(String username) {
        return revokePreviousAndGenerateNewToken(username, JwtToken.JwtTokenType.ACCESS, config.accessTtlSeconds());
    }

    @Logging(Level.INFO)
    @Override
    public String generateRefreshToken(String username) {
        return revokePreviousAndGenerateNewToken(username, JwtToken.JwtTokenType.REFRESH, config.refreshTtlSeconds());
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public void validateToken(String token, JwtToken.JwtTokenType tokenType) {
        InputDataValidator.validateNotBlank(token, "Token");
        checkSecretString();
        var jwtToken = DECODER.apply(token, config.secret());
        if (jwtToken.getPayload().getJtt() != tokenType) {
            throw new IllegalArgumentException("Invalid JWT token type");
        }
        var isValid = isTokenValid(jwtToken);
        if (!isValid) {
            throw new AuthenticationException("Invalid JWT token");
        }
        var tokenId = jwtToken.getId() != null ? jwtToken.getId() : jwtToken.getPayload().getJti();
        var isRevoked = tokenDao.isRevokedById(tokenId);
        if (isRevoked) {
            throw new AuthenticationException("JWT token is revoked");
        }
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public void revokeTokenIfExists(String username, JwtToken.JwtTokenType type) {
        InputDataValidator.validateNotBlank(username, "Username");
        tokenDao.findLastNotRevokedByUsernameAndType(username, type)
                .ifPresent(t -> tokenDao.revoke(t.getId()));
    }

    @Logging(Level.INFO)
    @Override
    public String getUsernameFromToken(String token) {
        InputDataValidator.validateNotBlank(token, "Token");
        checkSecretString();
        var jwtToken = DECODER.apply(token, config.secret());
        if (!isTokenValid(jwtToken)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        return jwtToken.getPayload().getSub();
    }

    private String revokePreviousAndGenerateNewToken(String username, JwtToken.JwtTokenType type, int ttlSeconds) {
        InputDataValidator.validateNotBlank(username, "Username");
        revokeTokenIfExists(username, type);
        var newToken = fetchJwtToken(username, type, ttlSeconds);
        tokenDao.save(newToken);
        newToken.getPayload().jti(newToken.getId());
        return ENCODER.apply(newToken);
    }

    private boolean isTokenValid(JwtToken jwtToken) {
        if (jwtToken.getHeader() == null || jwtToken.getPayload() == null) {
            return false;
        }
        return isTokenTypeValid(jwtToken)
                && isTokenIssuerValid(jwtToken)
                && isTokenAudienceValid(jwtToken)
                && isTokenSubjectValid(jwtToken)
                && !isTokenExpired(jwtToken);
    }

    private boolean isTokenSubjectValid(JwtToken jwtToken) {
        var tokenSubject = jwtToken.getPayload().getSub();
        return tokenSubject != null && !tokenSubject.isBlank();
    }

    private boolean isTokenAudienceValid(JwtToken jwtToken) {
        var tokenAudience = jwtToken.getPayload().getAud();
        return tokenAudience != null && !tokenAudience.isBlank() && tokenAudience.equals(config.audience());
    }

    private boolean isTokenIssuerValid(JwtToken jwtToken) {
        var tokenIssuer = jwtToken.getPayload().getIss();
        return tokenIssuer != null && !tokenIssuer.isBlank() && tokenIssuer.equals(config.issuer());
    }

    private boolean isTokenExpired(JwtToken jwtToken) {
        var expiration = jwtToken.getPayload().getExp();
        return expiration != null && expiration.isBefore(Instant.now());
    }

    private boolean isTokenTypeValid(JwtToken jwtToken) {
        var tokenType = jwtToken.getHeader().getTyp();
        return tokenType != null && !tokenType.isBlank() && tokenType.equals(JWT_TOKEN_TYPE);
    }

    private void checkSecretString() {
        if (config.secret() == null || config.secret().isBlank()) {
            throw new IllegalArgumentException("JWT secret must be configured");
        }
    }

    private JwtToken fetchJwtToken(String username, JwtToken.JwtTokenType type, int ttlSeconds) {
        return new JwtToken()
                .header(new JwtToken.Header()
                        .alg(config.encryptionAlgorithm())
                        .typ(JWT_TOKEN_TYPE))
                .payload(new JwtToken.Payload()
                        .jtt(type)
                        .iss(config.issuer())
                        .sub(username)
                        .aud(config.audience())
                        .iat(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                        .exp(Instant.now().plusSeconds(ttlSeconds).truncatedTo(ChronoUnit.SECONDS)))
                .secretKey(config.secret())
                .revoked(Boolean.FALSE);
    }

    @ConfigurationProperties(prefix = "app.security.jwt")
    public record JwtConfiguration(
            boolean enabled,
            String encryptionAlgorithm,
            String issuer,
            String audience,
            int accessTtlSeconds,
            int refreshTtlSeconds,
            String secret
    ) {}

}
