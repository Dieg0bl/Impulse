package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Participation Status Enumeration
 * 
 * Defines user participation states in challenges
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ParticipationStatus {
    
    /**
     * User enrolled in challenge but hasn't started
     */
    ENROLLED("Enrolled"),
    
    /**
     * User actively participating in challenge
     */
    ACTIVE("Active"),
    
    /**
     * User successfully completed the challenge
     */
    COMPLETED("Completed"),
    
    /**
     * User failed to complete the challenge
     */
    FAILED("Failed"),
    
    /**
     * User withdrew from the challenge
     */
    WITHDRAWN("Withdrawn");

    private final String displayName;

    ParticipationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if participation is active
     */
    public boolean isActive() {
        return this == ACTIVE || this == ENROLLED;
    }

    /**
     * Check if participation is finished
     */
    public boolean isFinished() {
        return this == COMPLETED || this == FAILED || this == WITHDRAWN;
    }

    /**
     * Check if participation was successful
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * Get valid next states for transitions
     */
    public ParticipationStatus[] getValidTransitions() {
        switch (this) {
            case ENROLLED:
                return new ParticipationStatus[]{ACTIVE, WITHDRAWN};
            case ACTIVE:
                return new ParticipationStatus[]{COMPLETED, FAILED, WITHDRAWN};
            case COMPLETED:
            case FAILED:
            case WITHDRAWN:
                return new ParticipationStatus[]{}; // Final states
            default:
                return new ParticipationStatus[]{};
        }
    }

    /**
     * Check if transition to target status is valid
     */
    public boolean canTransitionTo(ParticipationStatus target) {
        for (ParticipationStatus validStatus : getValidTransitions()) {
            if (validStatus == target) {
                return true;
            }
        }
        return false;
    }
}
