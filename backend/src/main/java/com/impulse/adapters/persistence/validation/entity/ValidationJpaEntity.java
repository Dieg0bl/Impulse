package com.impulse.adapters.persistence.validation.entity;

import com.impulse.domain.enums.ValidationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validations")
public class ValidationJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String evidenceId;
    @Column(nullable = false)
    private String validatorUserId;
    private String comments;
    @Enumerated(EnumType.STRING)
    private ValidationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEvidenceId() { return evidenceId; }
    public void setEvidenceId(String evidenceId) { this.evidenceId = evidenceId; }
    public String getValidatorUserId() { return validatorUserId; }
    public void setValidatorUserId(String validatorUserId) { this.validatorUserId = validatorUserId; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public ValidationStatus getStatus() { return status; }
    public void setStatus(ValidationStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

