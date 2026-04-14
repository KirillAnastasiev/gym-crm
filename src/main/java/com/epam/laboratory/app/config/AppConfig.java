package com.epam.laboratory.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.epam.laboratory.app")
public class AppConfig {
    @Bean
    public Map<String, Object> storage() {
        return new HashMap<>();
    }
}
