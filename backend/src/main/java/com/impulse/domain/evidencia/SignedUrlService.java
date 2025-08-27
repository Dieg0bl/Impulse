package com.impulse.domain.evidencia;

/**
 * Lightweight Signed URL helper used in mapping. In production this should be
 * a proper service that signs URLs (S3, GCS, etc.). Here we provide a static
 * helper to keep MapStruct expressions simple and compilable during CI.
 */
public final class SignedUrlService {
    private SignedUrlService() {}

    public static String signDownloadStatic(String url) {
        if (url == null) return null;
        // placeholder: real implementation will sign the URL
        return url + "?signed=1";
    }
}
