package com.epam.laboratory.app.security;

import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "username")
@ToString
public class JwtTokenPrincipal implements UserDetails, Principal {

    private String name;

    private String username;

    @ToString.Exclude
    private String password;

    private boolean active;

    private boolean expired;

    private Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return !expired;
    }
}
