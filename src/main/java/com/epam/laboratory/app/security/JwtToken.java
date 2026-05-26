package com.epam.laboratory.app.security;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jwt_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class JwtToken {
    public static final String ALG_CLAIM = "alg";
    public static final String TYP_CLAIM = "typ";
    public static final String JTI_CLAIM = "jti";
    public static final String JTT_CLAIM = "jtt";
    public static final String ISS_CLAIM = "iss";
    public static final String SUB_CLAIM = "sub";
    public static final String AUD_CLAIM = "aud";
    public static final String IAT_CLAIM = "iat";
    public static final String EXP_CLAIM = "exp";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Transient
    private Header header;

    @Embedded
    private Payload payload;

    @Transient
    private String secretKey;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked;

    public JwtToken id(UUID id) {
        this.id = id;
        return this;
    }

    public JwtToken header(Header header) {
        this.header = header;
        return this;
    }

    public JwtToken payload(Payload payload) {
        this.payload = payload;
        return this;
    }

    public JwtToken secretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public JwtToken revoked(Boolean revoked) {
        this.revoked = revoked;
        return this;
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class Header {

        private String alg;

        private String typ;

        public Header typ(String typ) {
            this.typ = typ;
            return this;
        }

        public Header alg(String alg) {
            this.alg = alg;
            return this;
        }


    }
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class Payload {

        @Column(name = "id", nullable = false, unique = true, insertable = false, updatable = false)
        private UUID jti;

        @Enumerated(EnumType.STRING)
        @Column(name = "token_type", nullable = false)
        private JwtTokenType jtt;

        @Transient
        private String iss;

        @Column(name = "username", nullable = false)
        private String sub;

        @Transient
        private String aud;

        @Transient
        private Instant iat;

        @Column(name = "expiry_date", nullable = false)
        private Instant exp;

        public Payload jti(UUID jti) {
            this.jti = jti;
            return this;
        }

        public Payload jtt(JwtTokenType jtt) {
            this.jtt = jtt;
            return this;
        }

        public Payload iss(String iss) {
            this.iss = iss;
            return this;
        }

        public Payload sub(String sub) {
            this.sub = sub;
            return this;
        }

        public Payload aud(String aud) {
            this.aud = aud;
            return this;
        }

        public Payload iat(Instant iat) {
            this.iat = iat;
            return this;
        }

        public Payload exp(Instant exp) {
            this.exp = exp;
            return this;
        }

    }

    public enum JwtTokenType {
        ACCESS, REFRESH
    }

}


















