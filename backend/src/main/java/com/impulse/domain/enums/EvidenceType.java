package com.impulse.domain.enums;

/**
 * Tipos de evidencia disponibles
 */
public enum EvidenceType {
    IMAGE("Image", "Imagen", "image/*", 5L * 1024 * 1024, "jpg,jpeg,png,gif,webp"),
    VIDEO("Video", "Video", "video/*", 50L * 1024 * 1024, "mp4,webm,mov,avi"),
    DOCUMENT("Document", "Documento", "application/*", 10L * 1024 * 1024, "pdf,doc,docx,txt"),
    LINK("Link", "Enlace web", "text/uri-list", 0, "http,https"),
    TEXT("Text", "Texto descriptivo", "text/plain", 0, "");

    private final String code;
    private final String description;
    private final String mimeTypePattern;
    private final long maxSizeBytes;
    private final String allowedExtensions;

    EvidenceType(String code, String description, String mimeTypePattern, 
                long maxSizeBytes, String allowedExtensions) {
        this.code = code;
        this.description = description;
        this.mimeTypePattern = mimeTypePattern;
        this.maxSizeBytes = maxSizeBytes;
        this.allowedExtensions = allowedExtensions;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
    public String getMimeTypePattern() { return mimeTypePattern; }
    public long getMaxSizeBytes() { return maxSizeBytes; }
    public String getAllowedExtensions() { return allowedExtensions; }

    /**
     * Verifica si requiere archivo
     */
    public boolean requiresFile() {
        return this != LINK && this != TEXT;
    }

    /**
     * Verifica si es multimedia
     */
    public boolean isMultimedia() {
        return this == IMAGE || this == VIDEO;
    }

    /**
     * Verifica si tiene límite de tamaño
     */
    public boolean hasSizeLimit() {
        return maxSizeBytes > 0;
    }

    /**
     * Formatea el tamaño máximo en formato legible
     */
    public String getFormattedMaxSize() {
        if (maxSizeBytes == 0) return "Sin límite";
        
        if (maxSizeBytes >= 1024 * 1024) {
            return (maxSizeBytes / (1024 * 1024)) + " MB";
        } else if (maxSizeBytes >= 1024) {
            return (maxSizeBytes / 1024) + " KB";
        } else {
            return maxSizeBytes + " bytes";
        }
    }

    /**
     * Verifica si una extensión es válida para este tipo
     */
    public boolean isValidExtension(String extension) {
        if (allowedExtensions.isEmpty()) return true;
        
        String ext = extension.toLowerCase().replaceFirst("^\\.", "");
        return allowedExtensions.toLowerCase().contains(ext);
    }
}
