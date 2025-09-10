package com.impulse.lean.infrastructure.storage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StorageService {

    private final StorageProperties props;

    private final Path localBase;

    public StorageService(StorageProperties props) {
        this.props = props;
        this.localBase = Paths.get(System.getProperty("user.home"), "impulse_storage");
        try {
            Files.createDirectories(localBase);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create local storage directory", e);
        }
    }

    public StorageUploadPresignResponse presign(String objectKey) {
        // For filesystem fallback we return a PUT URL that maps to a tiny endpoint (not implemented) or a fake URL
        if ("filesystem".equalsIgnoreCase(props.getProvider()) || props.getEndpoint() == null || props.getEndpoint().isBlank()) {
        String fakeUrl = UriComponentsBuilder.newInstance()
            .scheme("file")
            .host("localhost")
            .path(localBase.resolve(objectKey).toString())
            .build()
            .toUriString();
        return new StorageUploadPresignResponse(fakeUrl, objectKey);
        }

        // For S3-compatible providers we would create a presigned PUT URL using SDK - omitted for brevity
        // Return a sensible placeholder URL
    String url = props.getEndpoint() + "/" + props.getBucket() + "/" + objectKey + "?X-Dev-Presign=" + Instant.now().getEpochSecond();
    return new StorageUploadPresignResponse(url, objectKey);
    }

    public String storeFromBytes(String objectKey, byte[] data) throws IOException {
        Path out = localBase.resolve(objectKey);
        Files.createDirectories(out.getParent());
        try (FileOutputStream fos = new FileOutputStream(out.toFile())) {
            fos.write(data);
        }
        return out.toAbsolutePath().toString();
    }

    public String generateKey(String prefix, String originalFilename) {
        String ext = "";
        int i = originalFilename.lastIndexOf('.');
        if (i >= 0) ext = originalFilename.substring(i);
    return prefix + "/" + UUID.randomUUID().toString() + ext;
    }
}
