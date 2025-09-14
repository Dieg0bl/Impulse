package com.impulse.domain.enums;

/**
 * Tipos de evidencia soportados en la plataforma
 */
public enum EvidenceType {
    /**
     * Imagen/fotografía
     */
    IMAGE("Image", "image/*", 5 * 1024 * 1024), // 5MB

    /**
     * Video
     */
    VIDEO("Video", "video/*", 50 * 1024 * 1024), // 50MB

    /**
     * Documento de texto
     */
    DOCUMENT("Document", "application/pdf,application/msword", 10 * 1024 * 1024), // 10MB

    /**
     * Enlace web/URL
     */
    LINK("Link", "text/url", 0),

    /**
     * Texto libre
     */
    TEXT("Text", "text/plain", 10000); // 10k characters

    private final String displayName;
    private final String allowedMimeTypes;
    private final long maxSize;

    EvidenceType(String displayName, String allowedMimeTypes, long maxSize) {
        this.displayName = displayName;
        this.allowedMimeTypes = allowedMimeTypes;
        this.maxSize = maxSize;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAllowedMimeTypes() {
        return allowedMimeTypes;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public boolean isValidMimeType(String mimeType) {
        if (mimeType == null) return false;
        return allowedMimeTypes.contains(mimeType) || allowedMimeTypes.contains(mimeType.split("/")[0] + "/*");
    }
}
