package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

@jakarta.persistence.Entity
@Table(name = "users", schema = "public")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "firstName", "lastName", "username", "password", "active"})
public abstract class User implements Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @JsonProperty(value = "id", required = true)
    protected Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    @JsonProperty(value = "firstName", required = true)
    protected String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @JsonProperty(value = "lastName", required = true)
    protected String lastName;

    @Column(name = "username", nullable = false, unique = true, length = 110)
    @JsonProperty(value = "username", required = true)
    protected String username;

    @Column(name = "password", nullable = false, length = 100)
    @ToString.Exclude
    @JsonProperty(value = "password", required = true)
    protected String password;

    @Column(name = "is_active", nullable = false)
    @JsonProperty(value = "active", required = true)
    protected boolean active;

}
