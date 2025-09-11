package com.impulse.lean.domain.model;

import com.impulse.user.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Evidence Validation Domain Entity
 * 
 * Represents a validation performed on evidence by a validator
 * Supports peer validation, moderator validation, and automatic validation
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "evidence_validations",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_evidence_validator", columnNames = {"evidence_id", "validator_id"})
       },
       indexes = {
           @Index(name = "idx_validations_evidence_id", columnList = "evidence_id"),
           @Index(name = "idx_validations_validator_id", columnList = "validator_id"),
           @Index(name = "idx_validations_score", columnList = "score")
       })
public class EvidenceValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    private Evidence evidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_id")
    private User validator; // Can be null for automatic validations

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_type", nullable = false)
    private ValidationType validationType;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("1.00")
    @Column(name = "score", nullable = false, precision = 3, scale = 2)
    private BigDecimal score;

    @Size(max = 2000)
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "criteria_scores", columnDefinition = "JSON")
    private String criteriaScores; // JSON object with detailed scoring

    @DecimalMin("0.00")
    @DecimalMax("1.00")
    @Column(name = "confidence_level", precision = 3, scale = 2)
    private BigDecimal confidenceLevel; // AI confidence or validator certainty

    @CreationTimestamp
    @Column(name = "validated_at", nullable = false, updatable = false)
    private LocalDateTime validatedAt;

    // Constructors
    public EvidenceValidation() {}

    public EvidenceValidation(Evidence evidence, User validator, ValidationType type, BigDecimal score) {
        this.evidence = evidence;
        this.validator = validator;
        this.validationType = type;
        this.score = score;
    }

    // Business methods
    public boolean isPositive() {
        return score.compareTo(new BigDecimal("0.7")) >= 0; // Configurable threshold
    }

    public boolean isNegative() {
        return score.compareTo(new BigDecimal("0.3")) < 0; // Configurable threshold
    }

    public boolean isNeutral() {
        return !isPositive() && !isNegative();
    }

    public boolean isAutomaticValidation() {
        return validationType == ValidationType.AUTOMATIC;
    }

    public boolean isPeerValidation() {
        return validationType == ValidationType.PEER;
    }

    public boolean isModeratorValidation() {
        return validationType == ValidationType.MODERATOR;
    }

    public boolean isHighConfidence() {
        return confidenceLevel != null && confidenceLevel.compareTo(new BigDecimal("0.8")) >= 0;
    }

    public String getValidatorName() {
        if (validator == null) {
            return "System";
        }
        return validator.getFullName();
    }

    public boolean hasDetailedFeedback() {
        return feedback != null && feedback.trim().length() > 10;
    }

    public boolean hasCriteriaScoring() {
        return criteriaScores != null && !criteriaScores.trim().isEmpty();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Evidence getEvidence() { return evidence; }
    public void setEvidence(Evidence evidence) { this.evidence = evidence; }

    public User getValidator() { return validator; }
    public void setValidator(User validator) { this.validator = validator; }

    public ValidationType getValidationType() { return validationType; }
    public void setValidationType(ValidationType validationType) { this.validationType = validationType; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getCriteriaScores() { return criteriaScores; }
    public void setCriteriaScores(String criteriaScores) { this.criteriaScores = criteriaScores; }

    public BigDecimal getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(BigDecimal confidenceLevel) { this.confidenceLevel = confidenceLevel; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvidenceValidation that = (EvidenceValidation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EvidenceValidation{" +
                "id=" + id +
                ", validationType=" + validationType +
                ", score=" + score +
                ", validator=" + (validator != null ? validator.getUsername() : "System") +
                '}';
    }
}
