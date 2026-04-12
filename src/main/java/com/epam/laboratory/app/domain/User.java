package com.epam.laboratory.app.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class User {
    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    @ToString.Exclude
    private String password;

    private boolean isActive;
}
