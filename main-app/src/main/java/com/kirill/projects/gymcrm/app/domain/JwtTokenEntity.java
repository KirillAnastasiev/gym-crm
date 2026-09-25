package com.kirill.projects.gymcrm.app.domain;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jwt_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class JwtTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "token_type", nullable = false, length = 20)
    private String tokenType;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "username", nullable = false, length = 110)
    private String username;

    @Column(name = "revoked", nullable = false)
    private Boolean isRevoked;

}
