package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Validator Specialty Enum
 * 
 * Areas of expertise for validators
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ValidatorSpecialty {
    // Fitness specialties
    FITNESS_CARDIO("Cardiovascular Fitness"),
    FITNESS_STRENGTH("Strength Training"),
    FITNESS_FLEXIBILITY("Flexibility & Mobility"),
    FITNESS_ENDURANCE("Endurance Training"),
    FITNESS_SPORTS("Sports Performance"),

    // Nutrition specialties
    NUTRITION_GENERAL("General Nutrition"),
    NUTRITION_WEIGHT_LOSS("Weight Loss"),
    NUTRITION_MUSCLE_GAIN("Muscle Gain"),
    NUTRITION_SPECIAL_DIETS("Special Diets"),
    NUTRITION_SUPPLEMENTS("Supplements"),

    // Wellness specialties
    WELLNESS_MENTAL_HEALTH("Mental Health & Wellness"),
    WELLNESS_SLEEP("Sleep Quality"),
    WELLNESS_STRESS_MANAGEMENT("Stress Management"),
    WELLNESS_MINDFULNESS("Mindfulness & Meditation"),
    WELLNESS_RECOVERY("Recovery & Rest"),

    // Lifestyle specialties
    LIFESTYLE_HABITS("Habit Formation"),
    LIFESTYLE_PRODUCTIVITY("Productivity"),
    LIFESTYLE_TIME_MANAGEMENT("Time Management"),
    LIFESTYLE_SOCIAL("Social Connections"),
    LIFESTYLE_ENVIRONMENT("Environment & Sustainability"),

    // Education & Learning
    EDUCATION_SKILL_DEVELOPMENT("Skill Development"),
    EDUCATION_LANGUAGE("Language Learning"),
    EDUCATION_PROFESSIONAL("Professional Development"),
    EDUCATION_CREATIVE("Creative Skills"),
    EDUCATION_TECHNICAL("Technical Skills"),

    // Health specialties
    HEALTH_PREVENTIVE("Preventive Health"),
    HEALTH_CHRONIC_CONDITIONS("Chronic Conditions"),
    HEALTH_REHABILITATION("Rehabilitation"),
    HEALTH_ELDERLY("Elderly Care"),
    HEALTH_YOUTH("Youth Health"),

    // Generic specialties
    GENERAL("General Validation"),
    EXPERT("Expert Level"),
    MODERATOR("Content Moderation");

    private final String displayName;

    ValidatorSpecialty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        String name = this.name();
        if (name.startsWith("FITNESS_")) return "Fitness";
        if (name.startsWith("NUTRITION_")) return "Nutrition";
        if (name.startsWith("WELLNESS_")) return "Wellness";
        if (name.startsWith("LIFESTYLE_")) return "Lifestyle";
        if (name.startsWith("EDUCATION_")) return "Education";
        if (name.startsWith("HEALTH_")) return "Health";
        return "General";
    }

    public boolean isFitness() {
        return getCategory().equals("Fitness");
    }

    public boolean isNutrition() {
        return getCategory().equals("Nutrition");
    }

    public boolean isWellness() {
        return getCategory().equals("Wellness");
    }

    public boolean isLifestyle() {
        return getCategory().equals("Lifestyle");
    }

    public boolean isEducation() {
        return getCategory().equals("Education");
    }

    public boolean isHealth() {
        return getCategory().equals("Health");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
