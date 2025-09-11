package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Achievement Type Enumeration
 * 
 * Types of achievements based on earning mechanism
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum AchievementType {
    /**
     * Achievements earned by reaching specific thresholds
     */
    THRESHOLD("Threshold"),
    
    /**
     * Achievements earned by maintaining streaks
     */
    STREAK("Streak"),
    
    /**
     * Achievements earned for completing specific actions
     */
    COMPLETION("Completion"),
    
    /**
     * Achievements earned for participation
     */
    PARTICIPATION("Participation"),
    
    /**
     * Achievements earned for quality metrics
     */
    QUALITY("Quality"),
    
    /**
     * Special milestone achievements
     */
    MILESTONE("Milestone"),
    
    /**
     * Time-based achievements
     */
    TIME_BASED("Time Based"),
    
    /**
     * Community-based achievements
     */
    COMMUNITY("Community");

    private final String displayName;

    AchievementType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get achievement type by display name
     */
    public static AchievementType fromDisplayName(String displayName) {
        for (AchievementType type : values()) {
            if (type.displayName.equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown achievement type: " + displayName);
    }

    /**
     * Check if type is performance-based
     */
    public boolean isPerformanceBased() {
        return this == THRESHOLD || this == QUALITY || this == STREAK;
    }

    /**
     * Check if type is social-based
     */
    public boolean isSocialBased() {
        return this == COMMUNITY || this == PARTICIPATION;
    }

    /**
     * Get type description for UI
     */
    public String getDescription() {
        switch (this) {
            case THRESHOLD:
                return "Achieve specific numerical goals";
            case STREAK:
                return "Maintain consistent activity over time";
            case COMPLETION:
                return "Complete specific tasks or challenges";
            case PARTICIPATION:
                return "Engage with platform features";
            case QUALITY:
                return "Demonstrate high-quality contributions";
            case MILESTONE:
                return "Reach significant platform milestones";
            case TIME_BASED:
                return "Achievements related to time spent or duration";
            case COMMUNITY:
                return "Contribute positively to the community";
            default:
                return "Special achievement";
        }
    }

    /**
     * Get estimated difficulty level
     */
    public String getDifficulty() {
        switch (this) {
            case PARTICIPATION:
                return "Easy";
            case COMPLETION:
            case TIME_BASED:
                return "Medium";
            case THRESHOLD:
            case QUALITY:
                return "Hard";
            case STREAK:
            case MILESTONE:
            case COMMUNITY:
                return "Very Hard";
            default:
                return "Unknown";
        }
    }
}
