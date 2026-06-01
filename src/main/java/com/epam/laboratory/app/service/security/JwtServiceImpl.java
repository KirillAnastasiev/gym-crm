package com.epam.laboratory.app.service.security;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.repository.JwtTokenDao;
import com.epam.laboratory.app.security.JwtToken;
import com.epam.laboratory.app.security.JwtToken.JwtTokenType;
import com.epam.laboratory.app.security.mapper.JwtTokenMapper;
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

    private final JwtConfiguration config;
    private final JwtTokenDao tokenDao;
    private final JwtTokenMapper tokenMapper;

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
    public JwtToken createAccessToken(String username) {
        InputDataValidator.validateNotBlank(username, "Username");
        var token = fetchJwtToken(username, JwtTokenType.ACCESS, config.accessTtlSeconds());
        var tokenEntity = tokenMapper.toEntity(token);
        tokenDao.save(tokenEntity);
        token.getPayload().jti(tokenEntity.getId());
        return token;
    }

    @Logging(Level.INFO)
    @Override
    public JwtToken createRefreshToken(String username) {
        InputDataValidator.validateNotBlank(username, "Username");
        var token = fetchJwtToken(username, JwtTokenType.REFRESH, config.refreshTtlSeconds());
        var tokenEntity = tokenMapper.toEntity(token);
        tokenDao.save(tokenEntity);
        token.getPayload().jti(tokenEntity.getId());
        return token;
    }

    @Logging(Level.INFO)
    @Override
    public String serializeToken(JwtToken token) {
        InputDataValidator.validateNotNull(token, "Token");
        return ENCODER.apply(token);
    }

    @Logging(Level.INFO)
    @Override
    public JwtToken deserializeToken(String token) {
        InputDataValidator.validateNotBlank(token, "Token");
        checkSecretString();
        return DECODER.apply(token, config.secret());
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public void validateToken(JwtToken token, JwtTokenType expectedType) {
        InputDataValidator.validateNotNull(token, "Token");
        checkSecretString();
        if (!isTokenValid(token, expectedType)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        if (isTokenExpired(token)) {
            throw new AuthenticationException("JWT token is expired");
        }
        if (isTokenRevoked(token)) {
            throw new AuthenticationException("JWT token is revoked");
        }
    }

    @Logging(Level.INFO)
    @Override
    public void revokeToken(String username, JwtTokenType type) {
        InputDataValidator.validateNotBlank(username, "Username");
        tokenDao.findTopByUsernameAndTokenTypeAndIsRevokedFalseOrderByIdDesc(username, type.name())
                .ifPresent(t -> tokenDao.revokeByUsername(t.getUsername()));
    }

    @Logging(Level.INFO)
    @Override
    public void revokeTokens(String username) {
        revokeToken(username, JwtTokenType.ACCESS);
        revokeToken(username, JwtTokenType.REFRESH);
    }

    @Logging(Level.INFO)
    @Override
    public String getUsernameFromToken(JwtToken token) {
        InputDataValidator.validateNotNull(token, "Token");
        return token.getPayload().getSub();
    }

    @Logging(Level.INFO)
    @Override
    public boolean isTokenExpired(JwtToken token) {
        InputDataValidator.validateNotNull(token, "Token");
        var expiration = token.getPayload().getExp();
        return expiration != null && expiration.isBefore(Instant.now());
    }

    @Logging(Level.INFO)
    @Override
    public boolean isTokenRevoked(JwtToken token) {
        InputDataValidator.validateNotNull(token, "Token");
        var tokenEntity = tokenMapper.toEntity(token);
        var tokenId = tokenEntity.getId();
        if (tokenId != null) {
            return tokenDao.isRevokedById(tokenId);
        }
        return true;
    }

    private boolean isTokenValid(JwtToken jwtToken, JwtTokenType expectedType) {
        if (jwtToken.getHeader() == null || jwtToken.getPayload() == null) {
            return false;
        }
        return isJwtTypeValid(jwtToken)
                && isTokenTypeValid(jwtToken, expectedType)
                && isTokenIssuerValid(jwtToken)
                && isTokenAudienceValid(jwtToken)
                && isTokenSubjectValid(jwtToken)
                && !isTokenExpired(jwtToken);
    }

    private boolean isJwtTypeValid(JwtToken jwtToken) {
        var tokenType = jwtToken.getHeader().getTyp();
        return tokenType != null && !tokenType.isBlank() && tokenType.equals(JWT_TOKEN_TYPE);
    }

    private boolean isTokenTypeValid(JwtToken jwtToken, JwtTokenType expectedType) {
        var tokenType = jwtToken.getPayload().getJtt();
        return tokenType != null && tokenType.equals(expectedType);
    }

    private boolean isTokenIssuerValid(JwtToken jwtToken) {
        var tokenIssuer = jwtToken.getPayload().getIss();
        return tokenIssuer != null && !tokenIssuer.isBlank() && tokenIssuer.equals(config.issuer());
    }

    private boolean isTokenAudienceValid(JwtToken jwtToken) {
        var tokenAudience = jwtToken.getPayload().getAud();
        return tokenAudience != null && !tokenAudience.isBlank() && tokenAudience.equals(config.audience());
    }

    private boolean isTokenSubjectValid(JwtToken jwtToken) {
        var tokenSubject = jwtToken.getPayload().getSub();
        return tokenSubject != null && !tokenSubject.isBlank();
    }

    private void checkSecretString() {
        if (config.secret() == null || config.secret().isBlank()) {
            throw new IllegalArgumentException("JWT secret must be configured");
        }
    }

    private JwtToken fetchJwtToken(String username, JwtTokenType type, int ttlSeconds) {
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
