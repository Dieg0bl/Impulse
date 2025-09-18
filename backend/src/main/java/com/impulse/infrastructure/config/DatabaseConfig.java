package com.impulse.infrastructure.config;

import com.impulse.shared.annotations.Generated;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database Configuration
 * JPA repositories and transaction management
 */
@Generated
@Configuration
@EnableJpaRepositories(basePackages = {
    "com.impulse.features.*.adapters.out.jpa",
    "com.impulse.infrastructure.persistence.repositories"
})
@EnableTransactionManagement
public class DatabaseConfig {

    // TODO: Configure JPA settings
    // - Entity scanning for feature modules
    // - Connection pool configuration
    // - Flyway integration
}
