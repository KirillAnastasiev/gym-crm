package com.epam.laboratory.cucumber;

import com.epam.laboratory.cucumber.config.EnvironmentResolver;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@CucumberContextConfiguration
@ComponentScan(basePackages = "com.epam.laboratory.cucumber")
@ActiveProfiles(resolver = EnvironmentResolver.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CucumberTestSuite {

    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false)
                .configure(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .build();
    }

}


