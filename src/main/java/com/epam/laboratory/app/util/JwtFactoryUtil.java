package com.epam.laboratory.app.util;

import com.epam.laboratory.app.security.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static com.epam.laboratory.app.security.JwtToken.*;
import static io.jsonwebtoken.SignatureAlgorithm.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtFactoryUtil {

    public static String toJwtString(JwtToken jwtToken) {
        return Jwts.builder()
                .header()
                    .add(getHeaderClimes(jwtToken))
                .and()
                .claims()
                    .add(getPayloadClimes(jwtToken))
                .and()
                .signWith(getAlgorithm(jwtToken), getSignKey(jwtToken.getSecretKey()))
                .compact();
    }

    public static JwtToken fromJwtString(String token, String secretKey) {
        try {
            var rawJwt = Jwts.parser()
                    .verifyWith(getSignKey(secretKey))
                    .build()
                    .parse(token);

            Header header = rawJwt.getHeader();
            Claims payload = (Claims) rawJwt.getPayload();

            return new JwtToken()
                    .header(getHeaderFromClaims(header))
                    .payload(getPayloadFromClaims(payload))
                    .secretKey(secretKey);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    private static JwtToken.Header getHeaderFromClaims(Header header) {
        return new JwtToken.Header()
                .alg((String) header.get(ALG_CLAIM))
                .typ((String) header.get(TYP_CLAIM));
    }

    private static JwtToken.Payload getPayloadFromClaims(Claims claims) {
        return new JwtToken.Payload()
                .jti(UUID.fromString(claims.getId()))
                .jtt(JwtToken.JwtTokenType.valueOf((String) claims.get(JTT_CLAIM)))
                .iss(claims.getIssuer())
                .sub(claims.getSubject())
                .aud(String.join(",", claims.getAudience()))
                .iat(claims.getIssuedAt().toInstant())
                .exp(claims.getExpiration().toInstant());
    }

    private static SignatureAlgorithm getAlgorithm(JwtToken token) {
        var alg = token.getHeader().getAlg();
        return switch (alg) {
            case "HS256" -> HS256;
            case "HS384" -> HS384;
            case "HS512" -> HS512;
            default -> NONE;
        };
    }

    private static SecretKey getSignKey(String secret) {
        InputDataValidator.validateNotBlank(secret, "Secret key");
        var keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Secret key must be at least 256 bits (32 bytes) long");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static Map<String, Object> getPayloadClimes(JwtToken jwtToken) {
        return Map.of(
                JTI_CLAIM, jwtToken.getPayload().getJti().toString(),
                JTT_CLAIM, jwtToken.getPayload().getJtt().name(),
                ISS_CLAIM, jwtToken.getPayload().getIss(),
                SUB_CLAIM, jwtToken.getPayload().getSub(),
                AUD_CLAIM, jwtToken.getPayload().getAud(),
                IAT_CLAIM, Date.from(jwtToken.getPayload().getIat().truncatedTo(ChronoUnit.SECONDS)),
                EXP_CLAIM, Date.from(jwtToken.getPayload().getExp().truncatedTo(ChronoUnit.SECONDS))
        );
    }

    private static Map<String, String> getHeaderClimes(JwtToken jwtToken) {
        return Map.of(
                ALG_CLAIM, jwtToken.getHeader().getAlg(),
                TYP_CLAIM, jwtToken.getHeader().getTyp()
        );
    }

}
