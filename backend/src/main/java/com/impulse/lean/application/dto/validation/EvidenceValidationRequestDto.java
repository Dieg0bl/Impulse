package com.impulse.lean.application.dto.validation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.impulse.lean.domain.model.ConfidenceLevel;
import com.impulse.lean.domain.model.RecommendedAction;
import com.impulse.lean.domain.model.ValidationDecision;
import com.impulse.lean.domain.model.ValidationPriority;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Evidence Validation Request DTO
 * 
 * DTO for creating or updating evidence validations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Schema(description = "Request DTO for evidence validation operations")
public class EvidenceValidationRequestDto {

    @Schema(description = "Evidence UUID to validate", example = "550e8400-e29b-41d4-a716-446655440001")
    @NotBlank(message = "Evidence UUID is required")
    private String evidenceUuid;

    @Schema(description = "Validator UUID", example = "550e8400-e29b-41d4-a716-446655440002")
    @NotBlank(message = "Validator UUID is required")
    private String validatorUuid;

    @Schema(description = "Validation decision")
    private ValidationDecision decision;

    @Schema(description = "Validation comments", example = "Evidence meets all requirements and is well documented")
    @Size(max = 2000, message = "Comments cannot exceed 2000 characters")
    private String comments;

    @Schema(description = "Internal notes for validators", example = "Consider for future reference")
    @Size(max = 1000, message = "Internal notes cannot exceed 1000 characters")
    private String internalNotes;

    @Schema(description = "Feedback for evidence submitter", example = "Great work! Consider adding more detail to section 3.")
    @Size(max = 1500, message = "Feedback cannot exceed 1500 characters")
    private String feedbackForUser;

    @Schema(description = "Accuracy score (0.00-10.00)", example = "8.50")
    @DecimalMin(value = "0.00", message = "Accuracy score must be at least 0.00")
    @DecimalMax(value = "10.00", message = "Accuracy score cannot exceed 10.00")
    private BigDecimal accuracyScore;

    @Schema(description = "Completeness score (0.00-10.00)", example = "7.75")
    @DecimalMin(value = "0.00", message = "Completeness score must be at least 0.00")
    @DecimalMax(value = "10.00", message = "Completeness score cannot exceed 10.00")
    private BigDecimal completenessScore;

    @Schema(description = "Relevance score (0.00-10.00)", example = "9.00")
    @DecimalMin(value = "0.00", message = "Relevance score must be at least 0.00")
    @DecimalMax(value = "10.00", message = "Relevance score cannot exceed 10.00")
    private BigDecimal relevanceScore;

    @Schema(description = "Quality score (0.00-10.00)", example = "8.25")
    @DecimalMin(value = "0.00", message = "Quality score must be at least 0.00")
    @DecimalMax(value = "10.00", message = "Quality score cannot exceed 10.00")
    private BigDecimal qualityScore;

    @Schema(description = "Validation deadline")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;

    @Schema(description = "Validation priority")
    private ValidationPriority priority;

    @Schema(description = "Validator's confidence level in the decision")
    private ConfidenceLevel confidenceLevel;

    @Schema(description = "List of recommended actions")
    private List<RecommendedAction> recommendedActions;

    @Schema(description = "Whether validation requires additional review")
    private boolean requiresReview;

    @Schema(description = "Reason for requiring review", example = "Complex case requiring specialist opinion")
    @Size(max = 500, message = "Review reason cannot exceed 500 characters")
    private String reviewReason;

    // Constructors
    public EvidenceValidationRequestDto() {}

    public EvidenceValidationRequestDto(String evidenceUuid, String validatorUuid) {
        this.evidenceUuid = evidenceUuid;
        this.validatorUuid = validatorUuid;
        this.priority = ValidationPriority.NORMAL;
        this.confidenceLevel = ConfidenceLevel.MEDIUM;
    }

    // Getters and setters
    public String getEvidenceUuid() { return evidenceUuid; }
    public void setEvidenceUuid(String evidenceUuid) { this.evidenceUuid = evidenceUuid; }

    public String getValidatorUuid() { return validatorUuid; }
    public void setValidatorUuid(String validatorUuid) { this.validatorUuid = validatorUuid; }

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
}
