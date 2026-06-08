package com.epam.laboratory.app.metrics;

import com.epam.laboratory.app.service.health.ApplicationHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ =  @Autowired)
public class SystemHealthInfoContributor implements InfoContributor {

    private final ApplicationHealthService applicationHealthService;

    @Override
    public void contribute(Info.Builder builder) {
        var healthDetails = applicationHealthService.detailedHealthReport();
        builder.withDetail("system_health", healthDetails);
    }

}
