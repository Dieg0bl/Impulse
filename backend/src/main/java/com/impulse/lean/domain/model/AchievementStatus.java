package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Achievement Status Enumeration
 * 
 * Status states for achievements in the system
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum AchievementStatus {
    /**
     * Achievement is active and can be earned
     */
    ACTIVE("Active"),
    
    /**
     * Achievement is temporarily disabled
     */
    DISABLED("Disabled"),
    
    /**
     * Achievement is archived and no longer available
     */
    ARCHIVED("Archived"),
    
    /**
     * Achievement is in draft state (not yet published)
     */
    DRAFT("Draft"),
    
    /**
     * Achievement is scheduled to become active
     */
    SCHEDULED("Scheduled"),
    
    /**
     * Achievement has expired and is no longer available
     */
    EXPIRED("Expired");

    private final String displayName;

    AchievementStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get achievement status by display name
     */
    public static AchievementStatus fromDisplayName(String displayName) {
        for (AchievementStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown achievement status: " + displayName);
    }

    /**
     * Check if achievement is available for earning
     */
    public boolean isEarnable() {
        return this == ACTIVE;
    }

    /**
     * Check if achievement is visible to users
     */
    public boolean isVisible() {
        return this == ACTIVE || this == DISABLED || this == EXPIRED;
    }

    /**
     * Check if achievement can be edited
     */
    public boolean isEditable() {
        return this == DRAFT || this == DISABLED || this == SCHEDULED;
    }

    /**
     * Get status color for UI
     */
    public String getStatusColor() {
        switch (this) {
            case ACTIVE:
                return "#00b894"; // Green
            case DISABLED:
                return "#fdcb6e"; // Yellow
            case ARCHIVED:
                return "#636e72"; // Gray
            case DRAFT:
                return "#74b9ff"; // Blue
            case SCHEDULED:
                return "#a29bfe"; // Purple
            case EXPIRED:
                return "#fd79a8"; // Pink
            default:
                return "#636e72"; // Default gray
        }
    }

    /**
     * Get status icon for UI
     */
    public String getStatusIcon() {
        switch (this) {
            case ACTIVE:
                return "fas fa-check-circle";
            case DISABLED:
                return "fas fa-pause-circle";
            case ARCHIVED:
                return "fas fa-archive";
            case DRAFT:
                return "fas fa-edit";
            case SCHEDULED:
                return "fas fa-clock";
            case EXPIRED:
                return "fas fa-times-circle";
            default:
                return "fas fa-question-circle";
        }
    }

    /**
     * Get status description
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "Achievement is live and can be earned by users";
            case DISABLED:
                return "Achievement is temporarily disabled";
            case ARCHIVED:
                return "Achievement is archived and no longer available";
            case DRAFT:
                return "Achievement is in draft state and not yet published";
            case SCHEDULED:
                return "Achievement is scheduled to become active";
            case EXPIRED:
                return "Achievement has expired and is no longer available";
            default:
                return "Unknown status";
        }
    }
}
