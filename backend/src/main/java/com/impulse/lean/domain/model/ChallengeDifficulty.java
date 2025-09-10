package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Challenge Difficulty Enumeration
 * 
 * Defines difficulty levels with corresponding point multipliers
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ChallengeDifficulty {
    
    BEGINNER("Beginner", 1, "Easy to start, minimal commitment required"),
    INTERMEDIATE("Intermediate", 2, "Moderate effort and commitment needed"),
    ADVANCED("Advanced", 3, "Significant dedication and experience required"),
    EXPERT("Expert", 4, "Maximum challenge for experienced participants");

    private final String displayName;
    private final int pointsMultiplier;
    private final String description;

    ChallengeDifficulty(String displayName, int pointsMultiplier, String description) {
        this.displayName = displayName;
        this.pointsMultiplier = pointsMultiplier;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPointsMultiplier() {
        return pointsMultiplier;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get difficulty level (1-4)
     */
    public int getLevel() {
        return pointsMultiplier;
    }

    /**
     * Check if this difficulty is at least the specified level
     */
    public boolean isAtLeast(ChallengeDifficulty required) {
        return this.pointsMultiplier >= required.pointsMultiplier;
    }
}
