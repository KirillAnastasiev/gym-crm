package com.epam.laboratory.app.service.security;

import com.epam.laboratory.app.repository.AuthenticationDao;
import com.epam.laboratory.app.security.UserDetailsPrincipal;
import com.epam.laboratory.app.util.InputDataValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UsernamePasswordAuthenticationUserDetailsService implements UserDetailsService {

    private final AuthenticationDao authenticationDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        InputDataValidator.validateNotBlank(username, "Username");
        return authenticationDao.findUserByUsername(username)
                .map(user -> UserDetailsPrincipal.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                        .active(user.getActive())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }
}
