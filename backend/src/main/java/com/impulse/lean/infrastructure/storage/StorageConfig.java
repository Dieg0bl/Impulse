package com.impulse.lean.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Value("${impulse.storage.provider:filesystem}")
    private String provider;

    @Value("${impulse.storage.bucket:impulse}")
    private String bucket;

    @Value("${impulse.storage.endpoint:}")
    private String endpoint;

    @Value("${impulse.storage.accessKey:}")
    private String accessKey;

    @Value("${impulse.storage.secretKey:}")
    private String secretKey;

    @Bean
    public StorageProperties storageProperties() {
        return new StorageProperties(provider, bucket, endpoint, accessKey, secretKey);
    }
}
