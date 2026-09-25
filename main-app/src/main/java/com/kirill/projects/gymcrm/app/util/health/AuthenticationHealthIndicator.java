package com.kirill.projects.gymcrm.app.util.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Component
@Slf4j
public class AuthenticationHealthIndicator {

    @Value("${app.security.jwt.enabled:true}")
    private boolean isJwtEnabled;

    public Map<String, Object> getAuthHealthStatistic() {
        return Map.of(
                "status", getAuthServiceStatus(),
                "jwt_enabled", isJwtEnabled(),
                "security_level", getSecurityLevel(),
                "is_healthy", isAuthServiceHealthy()
        );
    }

    public boolean isAuthServiceHealthy() {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public HealthStatus getAuthServiceStatus() {
        return isAuthServiceHealthy() ? HealthStatus.UP : HealthStatus.DOWN;
    }

    public String getSecurityLevel() {
        return "Configured";
    }

    public boolean isJwtEnabled() {
        return isJwtEnabled;
    }

}

