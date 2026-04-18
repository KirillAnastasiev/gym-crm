package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "firstName", "lastName", "username", "password", "active"})
public abstract class User {
    @JsonProperty(value = "id", required = true)
    protected Long id;

    @JsonProperty(value = "firstName", required = true)
    protected String firstName;

    @JsonProperty(value = "lastName", required = true)
    protected String lastName;

    @JsonProperty(value = "username", required = true)
    protected String username;

    @ToString.Exclude
    @JsonProperty(value = "password", required = true)
    protected String password;

    @JsonProperty(value = "active", required = true)
    protected boolean active;
}
