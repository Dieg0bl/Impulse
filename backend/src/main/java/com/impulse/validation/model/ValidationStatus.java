package com.impulse.validation.model;

/**
 * IMPULSE LEAN v1 - Validation Status Enum
 * 
 * Represents the current status of an evidence validation process
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ValidationStatus {
    
    /**
     * Validation has been assigned but not yet started
     */
    PENDING("Pending", "Validation is assigned but not yet started"),
    
    /**
     * Validator is currently reviewing the evidence
     */
    IN_PROGRESS("In Progress", "Validator is actively reviewing the evidence"),
    
    /**
     * Validation is on hold, waiting for additional information
     */
    ON_HOLD("On Hold", "Validation paused, waiting for additional information"),
    
    /**
     * Validation requires escalation to senior validator or moderator
     */
    UNDER_REVIEW("Under Review", "Validation escalated for senior review"),
    
    /**
     * Validation has been completed with a decision
     */
    COMPLETED("Completed", "Validation finished with final decision"),
    
    /**
     * Validation was cancelled before completion
     */
    CANCELLED("Cancelled", "Validation process was cancelled"),
    
    /**
     * Validation has expired due to timeout
     */
    EXPIRED("Expired", "Validation deadline has passed");

    private final String displayName;
    private final String description;

    ValidationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this status represents an active validation
     */
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS || this == ON_HOLD || this == UNDER_REVIEW;
    }

    /**
     * Check if this status represents a completed validation
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * Check if this status represents a terminated validation
     */
    public boolean isTerminated() {
        return this == CANCELLED || this == EXPIRED;
    }

    /**
     * Check if validation can be started from this status
     */
    public boolean canStart() {
        return this == PENDING;
    }

    /**
     * Check if validation can be completed from this status
     */
    public boolean canComplete() {
        return this == IN_PROGRESS || this == UNDER_REVIEW;
    }

    /**
     * Check if validation can be put on hold from this status
     */
    public boolean canPutOnHold() {
        return this == IN_PROGRESS;
    }

    /**
     * Check if validation can be resumed from this status
     */
    public boolean canResume() {
        return this == ON_HOLD;
    }

    /**
     * Check if validation can be escalated from this status
     */
    public boolean canEscalate() {
        return this == IN_PROGRESS || this == ON_HOLD;
    }

    /**
     * Get next possible statuses from current status
     */
    public ValidationStatus[] getNextPossibleStatuses() {
        switch (this) {
            case PENDING:
                return new ValidationStatus[]{IN_PROGRESS, CANCELLED};
            case IN_PROGRESS:
                return new ValidationStatus[]{COMPLETED, ON_HOLD, UNDER_REVIEW, CANCELLED};
            case ON_HOLD:
                return new ValidationStatus[]{IN_PROGRESS, UNDER_REVIEW, CANCELLED};
            case UNDER_REVIEW:
                return new ValidationStatus[]{COMPLETED, IN_PROGRESS, CANCELLED};
            case COMPLETED:
            case CANCELLED:
            case EXPIRED:
                return new ValidationStatus[]{};
            default:
                return new ValidationStatus[]{};
        }
    }
}
