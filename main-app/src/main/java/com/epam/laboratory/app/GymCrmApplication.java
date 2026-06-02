package com.epam.laboratory.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.epam.laboratory.app")
public class GymCrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(GymCrmApplication.class, args);
    }
}
