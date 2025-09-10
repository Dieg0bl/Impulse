package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Challenge Status Enumeration
 * 
 * Defines challenge lifecycle states with state machine transitions
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ChallengeStatus {
    
    /**
     * Challenge created but not published yet
     */
    DRAFT("Draft"),
    
    /**
     * Challenge published and available for participation
     */
    PUBLISHED("Published"),
    
    /**
     * Challenge archived (no longer accepting participants)
     */
    ARCHIVED("Archived"),
    
    /**
     * Challenge soft deleted
     */
    DELETED("Deleted");

    private final String displayName;

    ChallengeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if challenge is available for participation
     */
    public boolean isAvailable() {
        return this == PUBLISHED;
    }

    /**
     * Check if challenge can be edited
     */
    public boolean isEditable() {
        return this == DRAFT;
    }

    /**
     * Check if challenge is visible to users
     */
    public boolean isVisible() {
        return this == PUBLISHED || this == ARCHIVED;
    }

    /**
     * Get valid next states for state machine transitions
     */
    public ChallengeStatus[] getValidTransitions() {
        switch (this) {
            case DRAFT:
                return new ChallengeStatus[]{PUBLISHED, DELETED};
            case PUBLISHED:
                return new ChallengeStatus[]{ARCHIVED, DELETED};
            case ARCHIVED:
                return new ChallengeStatus[]{PUBLISHED, DELETED};
            case DELETED:
                return new ChallengeStatus[]{}; // No transitions from deleted
            default:
                return new ChallengeStatus[]{};
        }
    }

    /**
     * Check if transition to target status is valid
     */
    public boolean canTransitionTo(ChallengeStatus target) {
        for (ChallengeStatus validStatus : getValidTransitions()) {
            if (validStatus == target) {
                return true;
            }
        }
        return false;
    }
}
