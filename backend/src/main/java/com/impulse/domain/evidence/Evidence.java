package com.impulse.domain.evidence;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evidence domain entity following DDD principles with JPA annotations
 */
@Entity
@Table(name = "evidence", indexes = {
    @Index(name = "idx_evidence_status", columnList = "status"),
    @Index(name = "idx_evidence_type", columnList = "evidence_type"),
    @Index(name = "idx_evidence_created", columnList = "created_at"),
    @Index(name = "idx_evidence_user", columnList = "user_id"),
    @Index(name = "idx_evidence_challenge", columnList = "challenge_id")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evidence {
    
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "evidence_type", nullable = false, length = 50)
    private String evidenceType;

    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EvidenceStatus status = EvidenceStatus.PENDING;

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private String userId;

    @Column(name = "challenge_id", nullable = false, columnDefinition = "CHAR(36)")
    private String challengeId;

    @Column(name = "validator_id", columnDefinition = "CHAR(36)")
    private String validatorId;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "validation_notes", columnDefinition = "TEXT")
    private String validationNotes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor for new evidence
    public Evidence(String title, String description, String evidenceType, 
                   String contentUrl, String userId, String challengeId) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.evidenceType = evidenceType;
        this.contentUrl = contentUrl;
        this.userId = userId;
        this.challengeId = challengeId;
        this.status = EvidenceStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isPending() {
        return this.status == EvidenceStatus.PENDING;
    }

    public boolean isValidated() {
        return this.status == EvidenceStatus.VALIDATED;
    }

    public boolean isRejected() {
        return this.status == EvidenceStatus.REJECTED;
    }

    public void validate(String validatorId, String validationNotes) {
        this.status = EvidenceStatus.VALIDATED;
        this.validatorId = validatorId;
        this.validationNotes = validationNotes;
        this.validatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String validatorId, String rejectionReason) {
        this.status = EvidenceStatus.REJECTED;
        this.validatorId = validatorId;
        this.validationNotes = rejectionReason;
        this.validatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void submitForReview() {
        if (this.status == EvidenceStatus.DRAFT) {
            this.status = EvidenceStatus.PENDING;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean canBeValidated() {
        return this.status == EvidenceStatus.PENDING;
    }

    public boolean hasValidator() {
        return this.validatorId != null;
    }

    public void updateContent(String contentUrl, String metadata) {
        this.contentUrl = contentUrl;
        this.metadata = metadata;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
