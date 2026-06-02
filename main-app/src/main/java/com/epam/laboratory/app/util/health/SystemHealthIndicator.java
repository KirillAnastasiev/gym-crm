package com.epam.laboratory.app.util.health;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SystemHealthIndicator {

    public Map<String, Object> systemHealthStatusStatistic() {
        return Map.of(
                "is_healthy", isServiceHealthy(),
                "memory_usage_percent", "%.2f%%".formatted(getMemoryUsagePercent()),
                "status", getServiceStatus()
        );
    }

    public HealthStatus getServiceStatus() {
        double memoryUsage = getMemoryUsagePercent();
        if (memoryUsage < 80) {
            return HealthStatus.UP;
        } else if (memoryUsage < 90) {
            return HealthStatus.DEGRADED;
        } else {
            return HealthStatus.DOWN;
        }
    }

    public boolean isServiceHealthy() {
        var memoryUsagePercent = getMemoryUsagePercent();
        return memoryUsagePercent < 90;
    }

    public double getMemoryUsagePercent() {
        var usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        var maxMemory = Runtime.getRuntime().maxMemory();
        return (double) usedMemory / maxMemory * 100;
    }
}

