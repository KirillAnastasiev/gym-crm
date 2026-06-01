package com.epam.laboratory.app.domain;

import com.epam.laboratory.app.dto.annotation.Sensitive;
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
