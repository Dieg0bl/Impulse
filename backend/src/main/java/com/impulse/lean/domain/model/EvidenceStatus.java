package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Evidence Status Enumeration
 * 
 * Defines evidence validation states
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum EvidenceStatus {
    
    /**
     * Evidence submitted but not yet validated
     */
    PENDING("Pending Validation"),
    
    /**
     * Evidence initially submitted to system
     */
    SUBMITTED("Submitted"),
    
    /**
     * Evidence approved by validators
     */
    APPROVED("Approved"),
    
    /**
     * Evidence rejected by validators
     */
    REJECTED("Rejected"),
    
    /**
     * Evidence flagged for review (inappropriate content)
     */
    FLAGGED("Flagged for Review");

    private final String displayName;

    EvidenceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if evidence is validated (final state)
     */
    public boolean isValidated() {
        return this == APPROVED || this == REJECTED;
    }

    /**
     * Check if evidence is approved
     */
    public boolean isApproved() {
        return this == APPROVED;
    }

    /**
     * Check if evidence needs attention
     */
    public boolean needsAttention() {
        return this == PENDING || this == FLAGGED;
    }

    /**
     * Check if evidence can be re-submitted
     */
    public boolean canResubmit() {
        return this == REJECTED;
    }

    /**
     * Get valid next states for transitions
     */
    public EvidenceStatus[] getValidTransitions() {
        switch (this) {
            case PENDING:
                return new EvidenceStatus[]{APPROVED, REJECTED, FLAGGED};
            case FLAGGED:
                return new EvidenceStatus[]{APPROVED, REJECTED};
            case APPROVED:
            case REJECTED:
                return new EvidenceStatus[]{}; // Final states
            default:
                return new EvidenceStatus[]{};
        }
    }

    /**
     * Check if transition to target status is valid
     */
    public boolean canTransitionTo(EvidenceStatus target) {
        for (EvidenceStatus validStatus : getValidTransitions()) {
            if (validStatus == target) {
                return true;
            }
        }
        return false;
    }
}
