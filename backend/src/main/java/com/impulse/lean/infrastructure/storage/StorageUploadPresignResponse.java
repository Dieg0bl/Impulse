package com.impulse.lean.infrastructure.storage;

public class StorageUploadPresignResponse {
    private String uploadUrl;
    private String objectKey;

    public StorageUploadPresignResponse() {}

    public StorageUploadPresignResponse(String uploadUrl, String objectKey) {
        this.uploadUrl = uploadUrl;
        this.objectKey = objectKey;
    }

    public String getUploadUrl() { return uploadUrl; }
    public void setUploadUrl(String uploadUrl) { this.uploadUrl = uploadUrl; }

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
}
