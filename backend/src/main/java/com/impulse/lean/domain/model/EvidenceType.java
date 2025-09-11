package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Evidence Type Enumeration
 * 
 * Defines supported evidence content types
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum EvidenceType {
    
    /**
     * Image evidence (photos, screenshots)
     */
    IMAGE("Image", "image/*"),
    
    /**
     * Video evidence (recordings, demonstrations)
     */
    VIDEO("Video", "video/*"),
    
    /**
     * Text evidence (descriptions, reflections)
     */
    TEXT("Text", "text/plain"),
    
    /**
     * File evidence (documents, PDFs)
     */
    FILE("File", "application/*"),
    
    /**
     * Link evidence (external URLs)
     */
    LINK("Link", "text/uri-list"),
    
    /**
     * Research publication evidence
     */
    RESEARCH_PUBLICATION("Research Publication", "application/pdf"),
    
    /**
     * Technical specification evidence
     */
    TECHNICAL_SPECIFICATION("Technical Specification", "application/pdf");

    private final String displayName;
    private final String defaultMimeType;

    EvidenceType(String displayName, String defaultMimeType) {
        this.displayName = displayName;
        this.defaultMimeType = defaultMimeType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultMimeType() {
        return defaultMimeType;
    }

    /**
     * Check if this type supports file uploads
     */
    public boolean supportsFileUpload() {
        return this == IMAGE || this == VIDEO || this == FILE;
    }

    /**
     * Check if this type requires validation
     */
    public boolean requiresValidation() {
        return this != TEXT; // Text might be self-validated
    }

    /**
     * Get maximum file size for this type (in bytes)
     */
    public long getMaxFileSize() {
        switch (this) {
            case IMAGE:
                return 10 * 1024 * 1024; // 10MB
            case VIDEO:
                return 100 * 1024 * 1024; // 100MB
            case FILE:
                return 25 * 1024 * 1024; // 25MB
            default:
                return 0; // No file size limit for text/link
        }
    }

    /**
     * Get allowed MIME types for this evidence type
     */
    public String[] getAllowedMimeTypes() {
        switch (this) {
            case IMAGE:
                return new String[]{"image/jpeg", "image/png", "image/gif", "image/webp"};
            case VIDEO:
                return new String[]{"video/mp4", "video/webm", "video/avi", "video/mov"};
            case FILE:
                return new String[]{"application/pdf", "application/msword", 
                                   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
            case TEXT:
                return new String[]{"text/plain"};
            case LINK:
                return new String[]{"text/uri-list"};
            default:
                return new String[]{};
        }
    }
}
