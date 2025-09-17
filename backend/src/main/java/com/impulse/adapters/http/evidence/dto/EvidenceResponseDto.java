package com.impulse.adapters.http.evidence.dto;

import com.impulse.domain.enums.EvidenceType;
import com.impulse.domain.enums.EvidenceStatus;
import java.time.LocalDateTime;

/**
 * EvidenceResponseDto - DTO de response para Evidence
 */
public class EvidenceResponseDto {

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

    // Constructor vacío requerido por Jackson para deserialización
    public EvidenceResponseDto() {
        // Constructor vacío para serialización/deserialización JSON
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public EvidenceType getType() { return type; }
    public void setType(EvidenceType type) { this.type = type; }

    public EvidenceStatus getStatus() { return status; }
    public void setStatus(EvidenceStatus status) { this.status = status; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public LocalDateTime getValidationDeadline() { return validationDeadline; }
    public void setValidationDeadline(LocalDateTime validationDeadline) { this.validationDeadline = validationDeadline; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
