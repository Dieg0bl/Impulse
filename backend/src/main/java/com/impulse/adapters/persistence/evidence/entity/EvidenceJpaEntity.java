package com.impulse.adapters.persistence.evidence.entity;

import com.impulse.domain.enums.EvidenceType;
import com.impulse.domain.enums.EvidenceStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * EvidenceJpaEntity - Entidad JPA para Evidence
 */
@Entity
@Table(name = "evidence")
public class EvidenceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EvidenceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EvidenceStatus status;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "validation_deadline")
    private LocalDateTime validationDeadline;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "challenge_id", nullable = false)
    private Long challengeId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor vacío requerido por JPA
    public EvidenceJpaEntity() {
        // Constructor vacío para JPA
    }

    // Constructor con parámetros principales
    public EvidenceJpaEntity(String title, EvidenceType type, EvidenceStatus status,
                            Long userId, Long challengeId) {
        this.title = title;
        this.type = type;
        this.status = status;
        this.userId = userId;
        this.challengeId = challengeId;
        this.submissionDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getChallengeId() { return challengeId; }
    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.submissionDate == null) {
            this.submissionDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

