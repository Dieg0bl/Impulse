package com.impulse.infrastructure.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Main Spring Boot entry point */
@SpringBootApplication(scanBasePackages = "com.impulse")
public class ImpulseApplication {
    public static void main(String[] args) { SpringApplication.run(ImpulseApplication.class, args); }
}
