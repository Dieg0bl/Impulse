package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Notification Priority Enum
 * 
 * Represents priority levels for notifications in the system
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum NotificationPriority {
    
    LOW("Low", "Low priority notification", 1),
    NORMAL("Normal", "Normal priority notification", 2),
    HIGH("High", "High priority notification", 3),
    URGENT("Urgent", "Urgent priority notification", 4),
    CRITICAL("Critical", "Critical priority notification", 5);

    private final String displayName;
    private final String description;
    private final int level;

    NotificationPriority(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Check if this priority is higher than another
     */
    public boolean isHigherThan(NotificationPriority other) {
        return this.level > other.level;
    }

    /**
     * Check if this priority is lower than another
     */
    public boolean isLowerThan(NotificationPriority other) {
        return this.level < other.level;
    }

    /**
     * Check if this priority requires immediate attention
     */
    public boolean requiresImmediateAttention() {
        return this == URGENT || this == CRITICAL;
    }

    /**
     * Check if this priority should bypass normal queuing
     */
    public boolean shouldBypassQueue() {
        return this == CRITICAL;
    }

    /**
     * Get delivery timeout in minutes based on priority
     */
    public int getDeliveryTimeoutMinutes() {
        switch (this) {
            case CRITICAL:
                return 1; // 1 minute
            case URGENT:
                return 5; // 5 minutes
            case HIGH:
                return 15; // 15 minutes
            case NORMAL:
                return 60; // 1 hour
            case LOW:
                return 240; // 4 hours
            default:
                return 60;
        }
    }

    /**
     * Get retry attempts based on priority
     */
    public int getMaxRetryAttempts() {
        switch (this) {
            case CRITICAL:
                return 5;
            case URGENT:
                return 3;
            case HIGH:
                return 2;
            case NORMAL:
                return 1;
            case LOW:
                return 1;
            default:
                return 1;
        }
    }

    /**
     * Get retry delay in seconds based on priority
     */
    public int getRetryDelaySeconds() {
        switch (this) {
            case CRITICAL:
                return 30; // 30 seconds
            case URGENT:
                return 60; // 1 minute
            case HIGH:
                return 180; // 3 minutes
            case NORMAL:
                return 300; // 5 minutes
            case LOW:
                return 600; // 10 minutes
            default:
                return 300;
        }
    }

    /**
     * Get CSS class for UI display
     */
    public String getCssClass() {
        switch (this) {
            case CRITICAL:
                return "priority-critical";
            case URGENT:
                return "priority-urgent";
            case HIGH:
                return "priority-high";
            case NORMAL:
                return "priority-normal";
            case LOW:
                return "priority-low";
            default:
                return "priority-normal";
        }
    }

    /**
     * Get color code for UI display
     */
    public String getColorCode() {
        switch (this) {
            case CRITICAL:
                return "#FF0000"; // Red
            case URGENT:
                return "#FF6600"; // Orange
            case HIGH:
                return "#FFCC00"; // Yellow
            case NORMAL:
                return "#0066CC"; // Blue
            case LOW:
                return "#999999"; // Gray
            default:
                return "#0066CC";
        }
    }
}
