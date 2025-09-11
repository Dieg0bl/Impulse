package com.impulse.validation.model;

/**
 * IMPULSE LEAN v1 - Validation Priority Enum
 * 
 * Priority levels for validation assignments
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ValidationPriority {
    LOW("Low", 1, "Low priority - can be completed within extended timeframe"),
    NORMAL("Normal", 2, "Normal priority - standard validation timeframe"),
    HIGH("High", 3, "High priority - requires faster completion"),
    URGENT("Urgent", 4, "Urgent priority - immediate attention required"),
    CRITICAL("Critical", 5, "Critical priority - must be completed ASAP");

    private final String displayName;
    private final int level;
    private final String description;

    ValidationPriority(String displayName, int level, String description) {
        this.displayName = displayName;
        this.level = level;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public boolean isLowPriority() {
        return this == LOW;
    }

    public boolean isHighPriority() {
        return this == HIGH || this == URGENT || this == CRITICAL;
    }

    public boolean isCritical() {
        return this == CRITICAL;
    }

    public boolean isUrgent() {
        return this == URGENT || this == CRITICAL;
    }

    public long getRecommendedTimeoutHours() {
        switch (this) {
            case LOW:
                return 168; // 7 days
            case NORMAL:
                return 72; // 3 days
            case HIGH:
                return 24; // 1 day
            case URGENT:
                return 8; // 8 hours
            case CRITICAL:
                return 2; // 2 hours
            default:
                return 72; // Default to normal
        }
    }

    public static ValidationPriority fromLevel(int level) {
        for (ValidationPriority priority : values()) {
            if (priority.level == level) {
                return priority;
            }
        }
        return NORMAL;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
