package com.epam.laboratory.app.security.filter;

import com.epam.laboratory.app.dto.MessageResponseDto;
import com.epam.laboratory.app.security.JwtAuthenticationConverter;
import com.epam.laboratory.app.service.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.epam.laboratory.app.config.SecurityConfig.ALLOWED_MATCHERS;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final RequestMatcher REQUEST_MATCHER = new NegatedRequestMatcher(ALLOWED_MATCHERS);

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final JwtAuthenticationConverter converter;
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!jwtService.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }
        if (REQUEST_MATCHER.matches(request)) {
            try {
                var preAuthToken = converter.convert(request);
                if (preAuthToken != null) {
                    var authentication = authenticationManager.authenticate(preAuthToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    returnUnauthorized(response, "Missing or invalid Authorization header");
                    return;
                }
            } catch (AuthenticationException e) {
                SecurityContextHolder.clearContext();
                returnUnauthorized(response, e.getMessage());
                return;
            } catch (Exception e) {
                returnUnauthorized(response, "Invalid JWT token");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void returnUnauthorized(HttpServletResponse response, String errorMessage) throws IOException {
        var responseDto = new MessageResponseDto(errorMessage != null ? errorMessage : "Unauthorized");
        var responseBody = objectMapper.writeValueAsString(responseDto);
        response.setHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"Access to the protected resource\", charset=\"UTF-8\"");
        response.getWriter().write(responseBody);
    }

}
