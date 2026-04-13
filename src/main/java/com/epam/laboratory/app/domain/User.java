package com.epam.laboratory.app.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class User {
    protected Long id;

    protected String firstName;

    protected String lastName;

    protected String username;

    @ToString.Exclude
    protected String password;

    protected boolean isActive;
}
