package com.kirill.projects.gymcrm.app.service.security;

import com.kirill.projects.gymcrm.app.aspect.annotation.Logging;
import com.kirill.projects.gymcrm.app.security.UserDetailsPrincipal;
import com.kirill.projects.gymcrm.app.util.InputDataValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UsernamePasswordAuthenticationUserDetailsService implements UserDetailsService {

    private final UserSecurityService userSecurityService;

    @Logging(Level.INFO)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        InputDataValidator.validateNotBlank(username, "Username");
        var optionalUser = userSecurityService.getUserWithUserSecurityByUsername(username);
        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            userSecurityService.checkUserSecurity(user);

            return UserDetailsPrincipal.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority("USER")))
                    .accountActive(user.getActive())
                    .accountLocked(user.getSecurity() != null && user.getSecurity().isAccountLocked())
                    .lockTime(user.getSecurity() != null ? user.getSecurity().getLockTime() : null)
                    .build();
        } else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }
}
