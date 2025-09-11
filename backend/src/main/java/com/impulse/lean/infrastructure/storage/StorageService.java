package com.impulse.lean.infrastructure.storage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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

        String provider = props.getProvider().toLowerCase();
        if (provider.contains("s3") || provider.contains("r2") || provider.contains("minio")) {
            // Build S3-compatible presigner using provided endpoint and credentials
            try (S3Presigner presigner = createPresigner()) {
                PutObjectRequest putReq = PutObjectRequest.builder()
                        .bucket(props.getBucket())
                        .key(objectKey)
                        .build();

                PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .putObjectRequest(putReq)
                        .build();

                PresignedPutObjectRequest signed = presigner.presignPutObject(presignRequest);
                return new StorageUploadPresignResponse(signed.url().toString(), objectKey);
            } catch (Exception ex) {
                // fallback to endpoint-based placeholder
                String url = props.getEndpoint() + "/" + props.getBucket() + "/" + objectKey + "?X-Dev-Presign=" + Instant.now().getEpochSecond();
                return new StorageUploadPresignResponse(url, objectKey);
            }
        }

        // Default placeholder
        String url = props.getEndpoint() + "/" + props.getBucket() + "/" + objectKey + "?X-Dev-Presign=" + Instant.now().getEpochSecond();
        return new StorageUploadPresignResponse(url, objectKey);
    }

    /**
     * Check whether an object exists in the configured storage. For filesystem provider it checks the local file.
     */
    public boolean headObjectExists(String objectKey) {
        if ("filesystem".equalsIgnoreCase(props.getProvider()) || props.getEndpoint() == null || props.getEndpoint().isBlank()) {
            Path p = localBase.resolve(objectKey);
            return Files.exists(p);
        }

        String provider = props.getProvider().toLowerCase();
        if (provider.contains("s3") || provider.contains("r2") || provider.contains("minio")) {
            try (S3Client client = createS3Client()) {
                HeadObjectRequest head = HeadObjectRequest.builder().bucket(props.getBucket()).key(objectKey).build();
                client.headObject(head);
                return true;
            } catch (NoSuchKeyException nk) {
                return false;
            } catch (Exception ex) {
                // on error be conservative and return false
                return false;
            }
        }

        return false;
    }

    private S3Client createS3Client() {
        software.amazon.awssdk.services.s3.S3ClientBuilder cb = S3Client.builder();
        if (props.getEndpoint() != null && !props.getEndpoint().isBlank()) cb.endpointOverride(URI.create(props.getEndpoint()));
        cb.region(Region.of("us-east-1"));
        if (props.getAccessKey() != null && !props.getAccessKey().isBlank()) {
            AwsBasicCredentials creds = AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());
            cb.credentialsProvider(StaticCredentialsProvider.create(creds));
        }
        return cb.build();
    }

    private S3Presigner createPresigner() {
        S3Presigner.Builder b = S3Presigner.builder();
        if (props.getEndpoint() != null && !props.getEndpoint().isBlank()) {
            b.endpointOverride(URI.create(props.getEndpoint()));
        }
        // region is required by SDK; use 'us-east-1' as default for compat
        b.region(Region.of("us-east-1"));

        if (props.getAccessKey() != null && !props.getAccessKey().isBlank()) {
            AwsBasicCredentials creds = AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());
            b.credentialsProvider(StaticCredentialsProvider.create(creds));
        }

    // default HTTP client
        return b.build();
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
