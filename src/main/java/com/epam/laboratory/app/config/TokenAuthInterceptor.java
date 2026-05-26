package com.epam.laboratory.app.config;

import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.security.JwtToken;
import com.epam.laboratory.app.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TokenAuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!jwtService.isEnabled()) {
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var token = authHeader.substring(7);
            jwtService.validateToken(token, JwtToken.JwtTokenType.ACCESS);
            return true;
        }
        throw new AuthenticationException("Missing authorization token");
    }

}
