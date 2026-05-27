package com.epam.laboratory.app.config;

import com.epam.laboratory.app.security.filter.JwtAuthenticationFilter;
import com.epam.laboratory.app.service.security.JwtAuthenticationUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfig {
    public static final RequestMatcher LOGIN_MATCHER = new RegexRequestMatcher("/api/auth/login", HttpMethod.POST.name());
    public static final RequestMatcher REFRESH_TOKENS_MATCHER = new RegexRequestMatcher("/api/auth/refresh-token", HttpMethod.POST.name());
    public static final RequestMatcher REGISTER_TRAINEE_MATCHER = new RegexRequestMatcher("/api/trainees", HttpMethod.POST.name());
    public static final RequestMatcher REGISTER_TRAINER_MATCHER = new RegexRequestMatcher("/api/trainers", HttpMethod.POST.name());
    public static final RequestMatcher ALLOWED_MATCHERS = new OrRequestMatcher(
            SecurityConfig.LOGIN_MATCHER,
            SecurityConfig.REFRESH_TOKENS_MATCHER,
            SecurityConfig.REGISTER_TRAINEE_MATCHER,
            SecurityConfig.REGISTER_TRAINER_MATCHER
    );


    @Bean
    public SecurityFilterChain configure(HttpSecurity http, JwtAuthenticationFilter jwtFilter) {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(LOGIN_MATCHER).permitAll()
                        .requestMatchers(REFRESH_TOKENS_MATCHER).permitAll()
                        .requestMatchers(REGISTER_TRAINEE_MATCHER).permitAll()
                        .requestMatchers(REGISTER_TRAINER_MATCHER).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll()
                )
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider preAuthenticatedAuthenticationProvider(JwtAuthenticationUserDetailsService userDetailsService) {
        var provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager preAuthenticatedAuthenticationManager(AuthenticationProvider preAuthenticatedAuthenticationProvider) {
        return new ProviderManager(preAuthenticatedAuthenticationProvider);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
