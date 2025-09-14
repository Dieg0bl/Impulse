package com.impulse.application.dto.evidence;

import java.time.LocalDateTime;

/**
 * DTO for Evidence Validation Response
 */
public class EvidenceValidationResponseDto {
    private Long id;
    private Long evidenceId;
    private Long validatorId;
    private String validatorUsername;
    private String status;
    private String decision;
    private String comments;
    private Integer score;
    private LocalDateTime validatedAt;
    private LocalDateTime createdAt;
    private Boolean isApproved;
    private String rejectionReason;

    // Constructors
    public EvidenceValidationResponseDto() {}

    public EvidenceValidationResponseDto(Long id, Long evidenceId, Long validatorId,
                                        String validatorUsername, String status, String decision,
                                        String comments, Integer score, LocalDateTime validatedAt,
                                        LocalDateTime createdAt, Boolean isApproved, String rejectionReason) {
        this.id = id;
        this.evidenceId = evidenceId;
        this.validatorId = validatorId;
        this.validatorUsername = validatorUsername;
        this.status = status;
        this.decision = decision;
        this.comments = comments;
        this.score = score;
        this.validatedAt = validatedAt;
        this.createdAt = createdAt;
        this.isApproved = isApproved;
        this.rejectionReason = rejectionReason;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }

    public Long getValidatorId() { return validatorId; }
    public void setValidatorId(Long validatorId) { this.validatorId = validatorId; }

    public String getValidatorUsername() { return validatorUsername; }
    public void setValidatorUsername(String validatorUsername) { this.validatorUsername = validatorUsername; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
