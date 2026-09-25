package com.kirill.projects.gymcrm.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@jakarta.persistence.Entity
@Table(name = "user_security")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class UserSecurity implements Entity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Column(name = "failed_attempts",  nullable = false)
    private int failedAttempts;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked;

    @Column(name = "lock_time")
    private Instant lockTime;

}
