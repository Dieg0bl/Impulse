package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Achievement Category Enumeration
 * 
 * Categories for grouping achievements in the gamification system
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum AchievementCategory {
    /**
     * Achievements related to challenge participation and completion
     */
    CHALLENGES("Challenges"),
    
    /**
     * Achievements related to evidence submission and quality
     */
    EVIDENCE("Evidence"),
    
    /**
     * Achievements related to validation activities
     */
    VALIDATION("Validation"),
    
    /**
     * Achievements related to social interactions and community
     */
    SOCIAL("Social"),
    
    /**
     * Achievements related to learning and skill development
     */
    LEARNING("Learning"),
    
    /**
     * Achievements related to consistency and streaks
     */
    CONSISTENCY("Consistency"),
    
    /**
     * Achievements related to milestones and special events
     */
    MILESTONES("Milestones"),
    
    /**
     * Achievements related to platform exploration
     */
    EXPLORATION("Exploration");

    private final String displayName;

    AchievementCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get achievement category by display name
     */
    public static AchievementCategory fromDisplayName(String displayName) {
        for (AchievementCategory category : values()) {
            if (category.displayName.equalsIgnoreCase(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown achievement category: " + displayName);
    }

    /**
     * Check if category is challenge-related
     */
    public boolean isChallengeRelated() {
        return this == CHALLENGES || this == EVIDENCE || this == VALIDATION;
    }

    /**
     * Check if category is social-related
     */
    public boolean isSocialRelated() {
        return this == SOCIAL || this == VALIDATION;
    }

    /**
     * Get category icon class for UI
     */
    public String getIconClass() {
        return switch (this) {
            case CHALLENGES -> "fas fa-trophy";
            case EVIDENCE -> "fas fa-file-alt";
            case VALIDATION -> "fas fa-check-circle";
            case SOCIAL -> "fas fa-users";
            case LEARNING -> "fas fa-graduation-cap";
            case CONSISTENCY -> "fas fa-calendar-check";
            case MILESTONES -> "fas fa-star";
            case EXPLORATION -> "fas fa-compass";
        };
    }

    /**
     * Get category color for UI
     */
    public String getColor() {
        return switch (this) {
            case CHALLENGES -> "#ff6b35";
            case EVIDENCE -> "#4ecdc4";
            case VALIDATION -> "#45b7d1";
            case SOCIAL -> "#f9ca24";
            case LEARNING -> "#6c5ce7";
            case CONSISTENCY -> "#00b894";
            case MILESTONES -> "#fdcb6e";
            case EXPLORATION -> "#e17055";
        };
    }
}
