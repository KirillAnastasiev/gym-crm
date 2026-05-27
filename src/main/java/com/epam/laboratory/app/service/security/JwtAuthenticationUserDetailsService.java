package com.epam.laboratory.app.service.security;

import com.epam.laboratory.app.security.JwtToken;
import com.epam.laboratory.app.security.JwtTokenPrincipal;
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
            var username = jwtService.getUsernameFromToken(jwtToken);
            var isTokenExpired = jwtService.isTokenExpired(jwtToken);
            var isTokenRevoked = jwtService.isTokenRevoked(jwtToken);
            return JwtTokenPrincipal.builder()
                    .name(authenticationToken.getName())
                    .username(username)
                    .authorities(List.of(new SimpleGrantedAuthority(jwtToken.getPayload().getJtt().name())))
                    .password("N/A")
                    .active(!isTokenRevoked)
                    .expired(isTokenExpired)
                    .build();
        }
        throw new UsernameNotFoundException("Invalid authentication token");
    }

}
