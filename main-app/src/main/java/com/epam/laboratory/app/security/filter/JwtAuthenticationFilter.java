package com.epam.laboratory.app.security.filter;

import com.epam.laboratory.app.security.JwtAuthenticationConverter;
import com.epam.laboratory.app.service.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.epam.laboratory.app.config.SecurityConfig.ALLOWED_MATCHERS;

@Component
@ConditionalOnProperty(name = "app.security.jwt.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final RequestMatcher REQUEST_MATCHER = new NegatedRequestMatcher(ALLOWED_MATCHERS);

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (jwtService.isEnabled() && REQUEST_MATCHER.matches(request)) {
            var converter = new JwtAuthenticationConverter(jwtService);
            var preAuthToken = converter.convert(request);
            if (preAuthToken instanceof PreAuthenticatedAuthenticationToken) {
                var authentication = authenticationManager.authenticate(preAuthToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new AuthenticationCredentialsNotFoundException("Missing or invalid Authorization header");
            }
        }
        chain.doFilter(request, response);
    }

}
