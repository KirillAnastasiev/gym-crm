package com.epam.laboratory.app.service.health;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.util.health.AuthenticationHealthIndicator;
import com.epam.laboratory.app.util.health.DatabaseHealthIndicator;
import com.epam.laboratory.app.util.health.SystemHealthIndicator;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ApplicationHealthServiceImpl implements ApplicationHealthService {

    @Value("${spring.application.name:GymCRM}")
    private String serviceName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private final AuthenticationHealthIndicator authenticationHealthIndicator;
    private final DatabaseHealthIndicator databaseHealthIndicator;
    private final SystemHealthIndicator systemHealthIndicator;

    @Logging(Level.INFO)
    @Override
    public Map<String, Object> systemHealthSummary() {
        var indicators = Map.of(
                "database", databaseHealthIndicator.getDatabaseStatus(),
                "service_availability", systemHealthIndicator.getServiceStatus(),
                "authentication_service", authenticationHealthIndicator.getAuthServiceStatus()
        );
        return Map.of(
                "service_name", serviceName(),
                "active_profile", activeProfile(),
                "timestamp", java.time.LocalDateTime.now(),
                "health_indicators", indicators
        );
    }

    @Logging(Level.INFO)
    @Override
    public Map<String, Object> systemHealthDetails() {
        return systemHealthIndicator.systemHealthStatusStatistic();
    }

    @Logging(Level.INFO)
    @Override
    public Map<String, Object> databaseHealthDetails() {
        return databaseHealthIndicator.databaseHealthStaistic();
    }

    @Logging(Level.INFO)
    @Override
    public Map<String, Object> authenticationHealthDetails() {
        return authenticationHealthIndicator.getAuthHealthStatistic();
    }

    @Logging(Level.INFO)
    @Override
    public Map<String, Object> detailedHealthReport() {
        return Map.of(
                "service_name", serviceName(),
                "active_profile", activeProfile(),
                "timestamp", java.time.LocalDateTime.now(),
                "system_health", systemHealthDetails(),
                "database_health", databaseHealthDetails(),
                "authentication_health", authenticationHealthDetails()
        );
    }

    private String serviceName() {
        return serviceName != null ? serviceName : "unknown";
    }

    private String activeProfile() {
        return activeProfile != null ? activeProfile : "unknown";
    }

}
