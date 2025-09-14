package com.impulse.application.dto.evidence;

import java.math.BigDecimal;

import com.impulse.domain.enums.ValidationType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Evidence Validation Request DTO
 *
 * Request DTO for validating evidence submissions
 *
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class EvidenceValidationRequestDto {

    @NotNull(message = "Validation type is required")
    private ValidationType validationType;

    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Score must be non-negative")
    @DecimalMax(value = "10.0", inclusive = true, message = "Score cannot exceed 10")
    private BigDecimal score;

    @Size(max = 2000, message = "Comments cannot exceed 2000 characters")
    private String comments;

    @Size(max = 1000, message = "Feedback cannot exceed 1000 characters")
    private String feedback;

    @DecimalMin(value = "0.0", inclusive = true, message = "Confidence level must be non-negative")
    @DecimalMax(value = "10.0", inclusive = true, message = "Confidence level cannot exceed 10")
    private BigDecimal confidenceLevel;

    private Boolean approved;

    @Size(max = 500, message = "Rejection reason cannot exceed 500 characters")
    private String rejectionReason;

    // Quality criteria scores
    private BigDecimal creativityScore;
    private BigDecimal completenessScore;
    private BigDecimal clarityScore;
    private BigDecimal relevanceScore;

    // Constructor
    public EvidenceValidationRequestDto() {
        // Default constructor for framework instantiation
    }

    public EvidenceValidationRequestDto(ValidationType validationType, BigDecimal score, String comments) {
        this.validationType = validationType;
        this.score = score;
        this.comments = comments;
    }

    // Validation methods
    public boolean isApprovalDecision() {
        return approved != null && approved;
    }

    public boolean isRejectionDecision() {
        return approved != null && !approved;
    }

    public boolean hasDetailedFeedback() {
        return feedback != null && !feedback.trim().isEmpty();
    }

    public boolean hasQualityCriteriaScores() {
        return creativityScore != null || completenessScore != null ||
               clarityScore != null || relevanceScore != null;
    }

    public BigDecimal getAverageQualityScore() {
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        if (creativityScore != null) {
            sum = sum.add(creativityScore);
            count++;
        }
        if (completenessScore != null) {
            sum = sum.add(completenessScore);
            count++;
        }
        if (clarityScore != null) {
            sum = sum.add(clarityScore);
            count++;
        }
        if (relevanceScore != null) {
            sum = sum.add(relevanceScore);
            count++;
        }

        return count > 0 ? sum.divide(new BigDecimal(count), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    // Getters and Setters
    public ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
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

    public BigDecimal getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(BigDecimal confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public BigDecimal getCreativityScore() {
        return creativityScore;
    }

    public void setCreativityScore(BigDecimal creativityScore) {
        this.creativityScore = creativityScore;
    }

    public BigDecimal getCompletenessScore() {
        return completenessScore;
    }

    public void setCompletenessScore(BigDecimal completenessScore) {
        this.completenessScore = completenessScore;
    }

    public BigDecimal getClarityScore() {
        return clarityScore;
    }

    public void setClarityScore(BigDecimal clarityScore) {
        this.clarityScore = clarityScore;
    }

    public BigDecimal getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(BigDecimal relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
}
