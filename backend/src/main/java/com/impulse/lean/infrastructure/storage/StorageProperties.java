package com.impulse.lean.infrastructure.storage;

public class StorageProperties {
    private final String provider;
    private final String bucket;
    private final String endpoint;
    private final String accessKey;
    private final String secretKey;

    public StorageProperties(String provider, String bucket, String endpoint, String accessKey, String secretKey) {
        this.provider = provider;
        this.bucket = bucket;
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getProvider() { return provider; }
    public String getBucket() { return bucket; }
    public String getEndpoint() { return endpoint; }
    public String getAccessKey() { return accessKey; }
    public String getSecretKey() { return secretKey; }
}
