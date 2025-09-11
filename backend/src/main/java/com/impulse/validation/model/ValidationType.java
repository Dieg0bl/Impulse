package com.impulse.validation.model;

/**
 * IMPULSE LEAN v1 - Validation Type Enumeration
 * 
 * Types of validation that can be performed on evidence
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ValidationType {
    /**
     * Automatic validation performed by the system/AI
     */
    AUTOMATIC("Automatic validation"),
    
    /**
     * Peer validation from community members
     */
    PEER("Peer validation"),
    
    /**
     * Moderator/expert validation
     */
    MODERATOR("Moderator validation"),
    
    /**
     * Self-assessment validation
     */
    SELF_ASSESSMENT("Self assessment"),
    
    /**
     * Manual validation by human reviewer
     */
    MANUAL("Manual validation"),
    
    /**
     * Expert validation by domain specialist
     */
    EXPERT("Expert validation");

    private final String displayName;

    ValidationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSystemValidation() {
        return this == AUTOMATIC;
    }

    public boolean isHumanValidation() {
        return this == PEER || this == MODERATOR || this == SELF_ASSESSMENT;
    }

    public boolean isExpertValidation() {
        return this == MODERATOR;
    }

    public boolean isPeerValidation() {
        return this == PEER;
    }

    public boolean isSelfValidation() {
        return this == SELF_ASSESSMENT;
    }

    public int getValidationWeight() {
        // Return validation weight for scoring calculations
        if (this == MODERATOR) {
            return 100;
        } else if (this == PEER) {
            return 70;
        } else if (this == AUTOMATIC) {
            return 50;
        } else if (this == SELF_ASSESSMENT) {
            return 30;
        }
        return 0;
    }

    public String getDescription() {
        String description;
        if (this == AUTOMATIC) {
            description = "Automated validation using AI and system rules";
        } else if (this == PEER) {
            description = "Validation performed by community peers";
        } else if (this == MODERATOR) {
            description = "Expert validation by platform moderators";
        } else if (this == SELF_ASSESSMENT) {
            description = "Self-assessment validation by the evidence submitter";
        } else {
            description = "Unknown validation type";
        }
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
