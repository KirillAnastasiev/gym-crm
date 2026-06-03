package com.epam.laboratory.app.config;

import com.epam.laboratory.app.security.CustomAuthenticationEntryPoint;
import com.epam.laboratory.app.security.filter.JwtAuthenticationFilter;
import com.epam.laboratory.app.service.security.JwtAuthenticationUserDetailsService;
import com.epam.laboratory.app.service.security.UsernamePasswordAuthenticationUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Configuration
public class SecurityConfig {
    public static final RequestMatcher SWAGGER_UI_MATCHER = new RegexRequestMatcher("^/(v3/api-docs(?:/.*)?|swagger-ui(?:/.*)?)$", null);
    public static final RequestMatcher GET_TOKENS_MATCHER = new RegexRequestMatcher("/api/auth/tokens", HttpMethod.GET.name());
    public static final RequestMatcher REFRESH_TOKENS_MATCHER = new RegexRequestMatcher("/api/auth/refresh-token", HttpMethod.POST.name());
    public static final RequestMatcher REGISTER_TRAINEE_MATCHER = new RegexRequestMatcher("/api/trainees", HttpMethod.POST.name());
    public static final RequestMatcher REGISTER_TRAINER_MATCHER = new RegexRequestMatcher("/api/trainers", HttpMethod.POST.name());
    public static final RequestMatcher ALLOWED_MATCHERS = new OrRequestMatcher(
                                                                    SWAGGER_UI_MATCHER,
                                                                    GET_TOKENS_MATCHER,
                                                                    REFRESH_TOKENS_MATCHER,
                                                                    REGISTER_TRAINEE_MATCHER,
                                                                    REGISTER_TRAINER_MATCHER
                                                          );

    @Bean
    public SecurityFilterChain configure(HttpSecurity http,
                                         JwtAuthenticationFilter jwtFilter,
                                         ObjectMapper objectMapper) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                            .requestMatchers(SWAGGER_UI_MATCHER).permitAll()
                            .requestMatchers(GET_TOKENS_MATCHER).authenticated()
                            .requestMatchers(REFRESH_TOKENS_MATCHER).permitAll()
                            .requestMatchers(REGISTER_TRAINEE_MATCHER).permitAll()
                            .requestMatchers(REGISTER_TRAINER_MATCHER).permitAll()
                            .requestMatchers("/api/**").authenticated()
                            .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(authException ->
                        authException
                                .defaultAuthenticationEntryPointFor(new CustomAuthenticationEntryPoint(objectMapper, "Basic realm=\"Access to the protected resource\", charset=\"UTF-8\""), GET_TOKENS_MATCHER)
                                .defaultAuthenticationEntryPointFor(new CustomAuthenticationEntryPoint(objectMapper, "Bearer realm=\"Access to the protected resource\", charset=\"UTF-8\""), new NegatedRequestMatcher(ALLOWED_MATCHERS)))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of(HttpHeaders.ACCEPT, HttpHeaders.CONTENT_TYPE, HttpHeaders.AUTHORIZATION));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider preAuthenticatedAuthenticationProvider(JwtAuthenticationUserDetailsService userDetailsService) {
        var provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider(UsernamePasswordAuthenticationUserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider preAuthenticatedAuthenticationProvider,
                                                       AuthenticationProvider daoAuthenticationProvider,
                                                       AuthenticationEventPublisher  authenticationEventPublisher) {
        var providerManager = new ProviderManager(List.of(preAuthenticatedAuthenticationProvider, daoAuthenticationProvider));
        providerManager.setAuthenticationEventPublisher(authenticationEventPublisher);
        return providerManager;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
