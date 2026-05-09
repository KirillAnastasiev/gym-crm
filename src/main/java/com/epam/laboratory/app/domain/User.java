package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public abstract class User implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq_gen")
    @SequenceGenerator(name = "users_id_seq_gen", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    protected Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    protected String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    protected String lastName;

    @Column(name = "username", nullable = false, unique = true, length = 110)
    protected String username;

    @Column(name = "password", nullable = false, length = 100)
    @ToString.Exclude
    protected String password;

    @Column(name = "is_active", nullable = false)
    protected Boolean active;

}
