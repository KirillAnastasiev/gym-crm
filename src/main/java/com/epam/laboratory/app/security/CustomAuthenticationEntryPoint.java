package com.epam.laboratory.app.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;
    private final String authenticationHeaderMessage;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        var problemDetail = ProblemDetail.forStatus(HttpServletResponse.SC_UNAUTHORIZED);
        problemDetail.setTitle("Unauthorized");
        problemDetail.setDetail("Missing or invalid Authorization header");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, authenticationHeaderMessage);
        mapper.writeValue(response.getOutputStream(), problemDetail);
    }

}
