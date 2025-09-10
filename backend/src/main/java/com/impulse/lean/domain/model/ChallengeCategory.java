package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Challenge Category Enumeration
 * 
 * Defines challenge categories for organization and discovery
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ChallengeCategory {
    
    FITNESS("Fitness & Health", "🏃‍♀️", "Physical fitness, exercise, sports, and health improvement"),
    NUTRITION("Nutrition & Diet", "🥗", "Healthy eating, cooking, meal planning, and nutrition education"),
    MINDFULNESS("Mindfulness & Mental Health", "🧘‍♂️", "Meditation, stress management, mental wellness, and self-care"),
    PRODUCTIVITY("Productivity & Organization", "📈", "Time management, goal setting, habits, and personal efficiency"),
    LEARNING("Learning & Skills", "📚", "Education, skill development, reading, and knowledge acquisition"),
    SOCIAL("Social & Community", "👥", "Relationships, networking, volunteering, and social activities"),
    CREATIVE("Creative & Artistic", "🎨", "Art, music, writing, crafts, and creative expression"),
    ENVIRONMENTAL("Environmental & Sustainability", "🌱", "Eco-friendly practices, sustainability, and environmental awareness");

    private final String displayName;
    private final String icon;
    private final String description;

    ChallengeCategory(String displayName, String icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get category by name (case insensitive)
     */
    public static ChallengeCategory fromString(String name) {
        for (ChallengeCategory category : values()) {
            if (category.name().equalsIgnoreCase(name) || 
                category.displayName.equalsIgnoreCase(name)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown challenge category: " + name);
    }
}
