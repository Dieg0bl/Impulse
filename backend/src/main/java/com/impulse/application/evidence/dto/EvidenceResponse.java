package com.impulse.application.evidence.dto;

import com.impulse.domain.enums.EvidenceType;
import com.impulse.domain.enums.EvidenceStatus;
import java.time.LocalDateTime;

/**
 * EvidenceResponse - DTO de respuesta para Evidence
 */
public class EvidenceResponse {

    private final String id;
    private final String title;
    private final String description;
    private final EvidenceType type;
    private final EvidenceStatus status;
    private final String fileUrl;
    private final String fileName;
    private final Long fileSize;
    private final String mimeType;
    private final LocalDateTime submissionDate;
    private final LocalDateTime validationDeadline;
    private final String userId;
    private final String challengeId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private EvidenceResponse(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.type = builder.type;
        this.status = builder.status;
        this.fileUrl = builder.fileUrl;
        this.fileName = builder.fileName;
        this.fileSize = builder.fileSize;
        this.mimeType = builder.mimeType;
        this.submissionDate = builder.submissionDate;
        this.validationDeadline = builder.validationDeadline;
        this.userId = builder.userId;
        this.challengeId = builder.challengeId;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public EvidenceType getType() { return type; }
    public EvidenceStatus getStatus() { return status; }
    public String getFileUrl() { return fileUrl; }
    public String getFileName() { return fileName; }
    public Long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public LocalDateTime getValidationDeadline() { return validationDeadline; }
    public String getUserId() { return userId; }
    public String getChallengeId() { return challengeId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String title;
        private String description;
        private EvidenceType type;
        private EvidenceStatus status;
        private String fileUrl;
        private String fileName;
        private Long fileSize;
        private String mimeType;
        private LocalDateTime submissionDate;
        private LocalDateTime validationDeadline;
        private String userId;
        private String challengeId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(EvidenceType type) { this.type = type; return this; }
        public Builder status(EvidenceStatus status) { this.status = status; return this; }
        public Builder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }
        public Builder fileName(String fileName) { this.fileName = fileName; return this; }
        public Builder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public Builder mimeType(String mimeType) { this.mimeType = mimeType; return this; }
        public Builder submissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; return this; }
        public Builder validationDeadline(LocalDateTime validationDeadline) { this.validationDeadline = validationDeadline; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder challengeId(String challengeId) { this.challengeId = challengeId; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public EvidenceResponse build() {
            return new EvidenceResponse(this);
        }
    }
}
