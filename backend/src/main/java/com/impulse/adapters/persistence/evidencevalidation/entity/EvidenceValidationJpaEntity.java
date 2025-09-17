package com.impulse.adapters.persistence.evidencevalidation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidence_validations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceValidationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "evidence_id", nullable = false)
    private UUID evidenceId;

    @Column(name = "validator_id", nullable = false)
    private UUID validatorId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "validation_type", length = 50)
    private String validationType;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "score", precision = 5, scale = 2)
    private Double score;

    @Column(name = "validation_criteria", columnDefinition = "JSON")
    private String validationCriteria;

    @Column(name = "validation_metadata", columnDefinition = "JSON")
    private String validationMetadata;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
