package com.impulse.domain.model;

import com.impulse.domain.enums.ValidationType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa la validación de una evidencia
 */
@Entity
@Table(name = "evidence_validations")
public class EvidenceValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evidence_id", nullable = false)
    private Long evidenceId;

    @Column(name = "validator_id", nullable = false)
    private Long validatorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_type", nullable = false)
    private ValidationType validationType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "score")
    private Integer score;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", insertable = false, updatable = false)
    private Evidence evidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_id", insertable = false, updatable = false)
    private User validator;

    // Constructors
    public EvidenceValidation() {
    }

    public EvidenceValidation(Long evidenceId, Long validatorId, ValidationType validationType, String status) {
        this.evidenceId = evidenceId;
        this.validatorId = validatorId;
        this.validationType = validationType;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(Long evidenceId) {
        this.evidenceId = evidenceId;
    }

    public Long getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(Long validatorId) {
        this.validatorId = validatorId;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    public User getValidator() {
        return validator;
    }

    public void setValidator(User validator) {
        this.validator = validator;
    }

    @Override
    public String toString() {
        return "EvidenceValidation{" +
                "id=" + id +
                ", evidenceId=" + evidenceId +
                ", validatorId=" + validatorId +
                ", validationType=" + validationType +
                ", status='" + status + '\'' +
                ", score=" + score +
                ", validatedAt=" + validatedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
