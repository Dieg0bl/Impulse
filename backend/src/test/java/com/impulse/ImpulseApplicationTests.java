package com.impulse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration test for IMPULSE application
 */
@SpringBootTest
@ActiveProfiles("test")
class ImpulseApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring Boot application context loads successfully
        // and all beans are properly configured
    }
}
