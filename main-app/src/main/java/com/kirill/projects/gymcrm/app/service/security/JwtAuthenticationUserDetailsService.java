package com.kirill.projects.gymcrm.app.service.security;

import com.kirill.projects.gymcrm.app.security.JwtToken;
import com.kirill.projects.gymcrm.app.security.JwtTokenPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JwtAuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final JwtService jwtService;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken) throws UsernameNotFoundException {
        if (authenticationToken.getPrincipal() instanceof JwtToken jwtToken) {
            try {
                var username = jwtService.getUsernameFromToken(jwtToken);
                var isTokenExpired = jwtService.isTokenExpired(jwtToken);
                var isTokenRevoked = jwtService.isTokenRevoked(jwtToken);
                return JwtTokenPrincipal.builder()
                        .username(username)
                        .authorities(List.of(new SimpleGrantedAuthority(jwtToken.getPayload().getJtt().name())))
                        .password("N/A")
                        .active(!isTokenRevoked)
                        .expired(isTokenExpired)
                        .build();
            } catch (Exception e) {
                throw new UsernameNotFoundException(e.getMessage(), e);
            }
        }
        throw new UsernameNotFoundException("Invalid authentication token");
    }

}
