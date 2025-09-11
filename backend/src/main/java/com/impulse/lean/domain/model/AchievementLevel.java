package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Achievement Level Enumeration
 * 
 * Difficulty and prestige levels for achievements
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum AchievementLevel {
    /**
     * Bronze level achievements - easiest to earn
     */
    BRONZE("Bronze", 1, "#cd7f32"),
    
    /**
     * Silver level achievements - moderate difficulty
     */
    SILVER("Silver", 2, "#c0c0c0"),
    
    /**
     * Gold level achievements - challenging to earn
     */
    GOLD("Gold", 3, "#ffd700"),
    
    /**
     * Platinum level achievements - very difficult
     */
    PLATINUM("Platinum", 4, "#e5e4e2"),
    
    /**
     * Diamond level achievements - extremely rare
     */
    DIAMOND("Diamond", 5, "#b9f2ff"),
    
    /**
     * Legendary achievements - the highest prestige
     */
    LEGENDARY("Legendary", 6, "#ff6348");

    private final String displayName;
    private final int tier;
    private final String color;

    AchievementLevel(String displayName, int tier, String color) {
        this.displayName = displayName;
        this.tier = tier;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getTier() {
        return tier;
    }

    public String getColor() {
        return color;
    }

    /**
     * Get achievement level by display name
     */
    public static AchievementLevel fromDisplayName(String displayName) {
        for (AchievementLevel level : values()) {
            if (level.displayName.equalsIgnoreCase(displayName)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown achievement level: " + displayName);
    }

    /**
     * Get achievement level by tier
     */
    public static AchievementLevel fromTier(int tier) {
        for (AchievementLevel level : values()) {
            if (level.tier == tier) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown achievement tier: " + tier);
    }

    /**
     * Get base points multiplier for this level
     */
    public double getPointsMultiplier() {
        switch (this) {
            case BRONZE:
                return 1.0;
            case SILVER:
                return 1.5;
            case GOLD:
                return 2.0;
            case PLATINUM:
                return 3.0;
            case DIAMOND:
                return 5.0;
            case LEGENDARY:
                return 10.0;
            default:
                return 1.0;
        }
    }

    /**
     * Get rarity description
     */
    public String getRarity() {
        switch (this) {
            case BRONZE:
                return "Common";
            case SILVER:
                return "Uncommon";
            case GOLD:
                return "Rare";
            case PLATINUM:
                return "Epic";
            case DIAMOND:
                return "Legendary";
            case LEGENDARY:
                return "Mythic";
            default:
                return "Unknown";
        }
    }

    /**
     * Get estimated percentage of users who earn this level
     */
    public String getEarnRate() {
        switch (this) {
            case BRONZE:
                return "70-90%";
            case SILVER:
                return "40-60%";
            case GOLD:
                return "15-25%";
            case PLATINUM:
                return "5-10%";
            case DIAMOND:
                return "1-3%";
            case LEGENDARY:
                return "<1%";
            default:
                return "Unknown";
        }
    }

    /**
     * Check if this level is higher than another
     */
    public boolean isHigherThan(AchievementLevel other) {
        return this.tier > other.tier;
    }

    /**
     * Check if this level is lower than another
     */
    public boolean isLowerThan(AchievementLevel other) {
        return this.tier < other.tier;
    }

    /**
     * Get next level (null if already at highest)
     */
    public AchievementLevel getNextLevel() {
        for (AchievementLevel level : values()) {
            if (level.tier == this.tier + 1) {
                return level;
            }
        }
        return null;
    }

    /**
     * Get previous level (null if already at lowest)
     */
    public AchievementLevel getPreviousLevel() {
        for (AchievementLevel level : values()) {
            if (level.tier == this.tier - 1) {
                return level;
            }
        }
        return null;
    }
}
