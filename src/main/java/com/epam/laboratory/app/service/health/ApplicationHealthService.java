package com.epam.laboratory.app.service.health;

import java.util.Map;

public interface ApplicationHealthService {
    Map<String, Object> systemHealthSummary();
    Map<String, Object> systemHealthDetails();
    Map<String, Object> databaseHealthDetails();
    Map<String, Object> authenticationHealthDetails();
    Map<String, Object> detailedHealthReport();
}
