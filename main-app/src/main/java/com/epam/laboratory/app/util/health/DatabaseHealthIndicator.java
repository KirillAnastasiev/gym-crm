package com.epam.laboratory.app.util.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class DatabaseHealthIndicator {

    @Value("${spring.jpa.database:default_db}")
    private String databaseName;

    private final DataSource dataSource;

    public Map<String, Object> databaseHealthStaistic() {
        return Map.of(
                "is_healthy", isDatabaseHealthy(),
                "db_name", getDatabaseName(),
                "status", getDatabaseStatus()
        );
    }

    public String getDatabaseName() {
        return databaseName != null ? databaseName : "unknown";
    }

    public boolean isDatabaseHealthy() {
        try (var connection = dataSource.getConnection()) {
            if (connection != null && connection.isValid(2)) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public HealthStatus getDatabaseStatus() {
        return isDatabaseHealthy() ? HealthStatus.UP : HealthStatus.DOWN;
    }

}



