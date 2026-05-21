package com.epam.laboratory.app.config;

import com.epam.laboratory.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ApplicationConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;

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

    @Bean
    public JacksonJsonHttpMessageConverter jacksonJsonHttpMessageConverter(JsonMapper objectMapper) {
        return new JacksonJsonHttpMessageConverter(objectMapper);
    }

    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        builder.configureMessageConvertersList(converters ->
                        converters.addFirst(jacksonJsonHttpMessageConverter(jsonMapper()))).build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new TokenAuthInterceptor(jwtUtil))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/refresh-token", "/api/trainees", "/api/trainers");
    }

}
