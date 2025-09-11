package com.impulse.lean.application.dto.validation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.impulse.lean.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * IMPULSE LEAN v1 - Evidence Validation Response DTO
 * 
 * DTO for evidence validation responses
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Schema(description = "Response DTO for evidence validation operations")
public class EvidenceValidationResponseDto {

    @Schema(description = "Validation UUID", example = "550e8400-e29b-41d4-a716-446655440003")
    private String uuid;

    @Schema(description = "Evidence UUID", example = "550e8400-e29b-41d4-a716-446655440001")
    private String evidenceUuid;

    @Schema(description = "Evidence title", example = "Process Improvement Documentation")
    private String evidenceTitle;

    @Schema(description = "Validator UUID", example = "550e8400-e29b-41d4-a716-446655440002")
    private String validatorUuid;

    @Schema(description = "Validator name", example = "Dr. Jane Smith")
    private String validatorName;

    @Schema(description = "Assignment UUID", example = "550e8400-e29b-41d4-a716-446655440004")
    private String assignmentUuid;

    @Schema(description = "Current validation status")
    private ValidationStatus status;

    @Schema(description = "Validation decision")
    private ValidationDecision decision;

    @Schema(description = "Validation comments")
    private String comments;

    @Schema(description = "Internal notes for validators")
    private String internalNotes;

    @Schema(description = "Feedback for evidence submitter")
    private String feedbackForUser;

    @Schema(description = "Accuracy score (0.00-10.00)", example = "8.50")
    private BigDecimal accuracyScore;

    @Schema(description = "Completeness score (0.00-10.00)", example = "7.75")
    private BigDecimal completenessScore;

    @Schema(description = "Relevance score (0.00-10.00)", example = "9.00")
    private BigDecimal relevanceScore;

    @Schema(description = "Quality score (0.00-10.00)", example = "8.25")
    private BigDecimal qualityScore;

    @Schema(description = "Overall score (0.00-10.00)", example = "8.38")
    private BigDecimal overallScore;

    @Schema(description = "When validation was started")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;

    @Schema(description = "When validation was completed")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    @Schema(description = "Validation deadline")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;

    @Schema(description = "Validation priority")
    private ValidationPriority priority;

    @Schema(description = "Validator's confidence level")
    private ConfidenceLevel confidenceLevel;

    @Schema(description = "List of recommended actions")
    private List<RecommendedAction> recommendedActions;

    @Schema(description = "Whether validation requires additional review")
    private boolean requiresReview;

    @Schema(description = "Reason for requiring review")
    private String reviewReason;

    @Schema(description = "Validation duration in minutes")
    private long durationInMinutes;

    @Schema(description = "Whether validation is overdue")
    private boolean overdue;

    @Schema(description = "When validation was created")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "When validation was last updated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "Who created the validation")
    private String createdBy;

    @Schema(description = "Who last updated the validation")
    private String updatedBy;

    // Constructors
    public EvidenceValidationResponseDto() {}

    /**
     * Create DTO from EvidenceValidation entity
     */
    public static EvidenceValidationResponseDto fromEntity(EvidenceValidation validation) {
        EvidenceValidationResponseDto dto = new EvidenceValidationResponseDto();
        
        dto.setUuid(validation.getUuid());
        dto.setEvidenceUuid(validation.getEvidence().getUuid());
        dto.setEvidenceTitle(validation.getEvidence().getTitle());
        dto.setValidatorUuid(validation.getValidator().getUuid());
        dto.setValidatorName(validation.getValidator().getUser().getFullName());
        
        if (validation.getAssignment() != null) {
            dto.setAssignmentUuid(validation.getAssignment().getUuid());
        }
        
        dto.setStatus(validation.getStatus());
        dto.setDecision(validation.getDecision());
        dto.setComments(validation.getComments());
        dto.setInternalNotes(validation.getInternalNotes());
        dto.setFeedbackForUser(validation.getFeedbackForUser());
        dto.setAccuracyScore(validation.getAccuracyScore());
        dto.setCompletenessScore(validation.getCompletenessScore());
        dto.setRelevanceScore(validation.getRelevanceScore());
        dto.setQualityScore(validation.getQualityScore());
        dto.setOverallScore(validation.getOverallScore());
        dto.setStartedAt(validation.getStartedAt());
        dto.setCompletedAt(validation.getCompletedAt());
        dto.setDeadline(validation.getDeadline());
        dto.setPriority(validation.getPriority());
        dto.setConfidenceLevel(validation.getConfidenceLevel());
        dto.setRecommendedActions(validation.getRecommendedActions());
        dto.setRequiresReview(validation.isRequiresReview());
        dto.setReviewReason(validation.getReviewReason());
        dto.setDurationInMinutes(validation.getValidationDurationInMinutes());
        dto.setOverdue(validation.isOverdue());
        dto.setCreatedAt(validation.getCreatedAt());
        dto.setUpdatedAt(validation.getUpdatedAt());
        dto.setCreatedBy(validation.getCreatedBy());
        dto.setUpdatedBy(validation.getUpdatedBy());
        
        return dto;
    }

    // Getters and setters
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getEvidenceUuid() { return evidenceUuid; }
    public void setEvidenceUuid(String evidenceUuid) { this.evidenceUuid = evidenceUuid; }

    public String getEvidenceTitle() { return evidenceTitle; }
    public void setEvidenceTitle(String evidenceTitle) { this.evidenceTitle = evidenceTitle; }

    public String getValidatorUuid() { return validatorUuid; }
    public void setValidatorUuid(String validatorUuid) { this.validatorUuid = validatorUuid; }

    public String getValidatorName() { return validatorName; }
    public void setValidatorName(String validatorName) { this.validatorName = validatorName; }

    public String getAssignmentUuid() { return assignmentUuid; }
    public void setAssignmentUuid(String assignmentUuid) { this.assignmentUuid = assignmentUuid; }

    public ValidationStatus getStatus() { return status; }
    public void setStatus(ValidationStatus status) { this.status = status; }

    public ValidationDecision getDecision() { return decision; }
    public void setDecision(ValidationDecision decision) { this.decision = decision; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    public String getFeedbackForUser() { return feedbackForUser; }
    public void setFeedbackForUser(String feedbackForUser) { this.feedbackForUser = feedbackForUser; }

    public BigDecimal getAccuracyScore() { return accuracyScore; }
    public void setAccuracyScore(BigDecimal accuracyScore) { this.accuracyScore = accuracyScore; }

    public BigDecimal getCompletenessScore() { return completenessScore; }
    public void setCompletenessScore(BigDecimal completenessScore) { this.completenessScore = completenessScore; }

    public BigDecimal getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(BigDecimal relevanceScore) { this.relevanceScore = relevanceScore; }

    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }

    public BigDecimal getOverallScore() { return overallScore; }
    public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public ValidationPriority getPriority() { return priority; }
    public void setPriority(ValidationPriority priority) { this.priority = priority; }

    public ConfidenceLevel getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(ConfidenceLevel confidenceLevel) { this.confidenceLevel = confidenceLevel; }

    public List<RecommendedAction> getRecommendedActions() { return recommendedActions; }
    public void setRecommendedActions(List<RecommendedAction> recommendedActions) { this.recommendedActions = recommendedActions; }

    public boolean isRequiresReview() { return requiresReview; }
    public void setRequiresReview(boolean requiresReview) { this.requiresReview = requiresReview; }

    public String getReviewReason() { return reviewReason; }
    public void setReviewReason(String reviewReason) { this.reviewReason = reviewReason; }

    public long getDurationInMinutes() { return durationInMinutes; }
    public void setDurationInMinutes(long durationInMinutes) { this.durationInMinutes = durationInMinutes; }

    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
