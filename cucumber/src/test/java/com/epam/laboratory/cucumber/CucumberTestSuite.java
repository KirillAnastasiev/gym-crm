package com.epam.laboratory.cucumber;

import com.epam.laboratory.cucumber.config.EnvironmentResolver;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.json.JsonMapper;

@CucumberContextConfiguration
@ComponentScan(basePackages = "com.epam.laboratory.cucumber")
@ActiveProfiles(resolver = EnvironmentResolver.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CucumberTestSuite {

    @Bean
    public JsonMapper jsonMapper() {
        return new JsonMapper();
    }

}


