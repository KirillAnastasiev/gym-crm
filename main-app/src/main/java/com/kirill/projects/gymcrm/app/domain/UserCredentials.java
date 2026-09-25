package com.kirill.projects.gymcrm.app.domain;

import com.kirill.projects.gymcrm.app.dto.annotation.Sensitive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserCredentials {
    private String username;

    @ToString.Exclude
    @Sensitive
    private String password;
}
