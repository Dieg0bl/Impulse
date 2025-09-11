package com.impulse.validation.model;

import com.impulse.user.model.UserRole;

/**
 * IMPULSE LEAN v1 - Validation Method Enumeration
 * 
 * Defines how evidence should be validated
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ValidationMethod {
    
    /**
     * Automatic validation using AI/algorithms
     */
    AUTOMATIC("Automatic", "System validates evidence automatically"),
    
    /**
     * Peer validation by community members
     */
    PEER("Peer Review", "Community members validate each other's evidence"),
    
    /**
     * Moderator validation by platform moderators
     */
    MODERATOR("Moderator Review", "Platform moderators validate evidence"),
    
    /**
     * Self validation by participant
     */
    SELF("Self Validation", "Participant validates their own progress");

    private final String displayName;
    private final String description;

    ValidationMethod(String displayName, String description) {
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
     * Check if this method requires human validation
     */
    public boolean requiresHumanValidation() {
        return this == PEER || this == MODERATOR;
    }

    /**
     * Check if this method is automated
     */
    public boolean isAutomated() {
        return this == AUTOMATIC;
    }

    /**
     * Get minimum validator role required
     */
    public UserRole getMinimumValidatorRole() {
        switch (this) {
            case MODERATOR:
                return UserRole.MODERATOR;
            case PEER:
                return UserRole.VALIDATOR;
            case AUTOMATIC:
            case SELF:
            default:
                return UserRole.USER;
        }
    }
}
