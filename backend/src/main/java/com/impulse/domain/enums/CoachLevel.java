package com.impulse.domain.enums;

/**
 * Niveles de coach en la plataforma
 */
public enum CoachLevel {
    /**
     * Coach novato - recién empezando
     */
    ROOKIE("Rookie", "New coach starting their journey", 0, 10, "🌱"),

    /**
     * Coach bronce - nivel básico
     */
    BRONZE("Bronze", "Basic level coach", 11, 50, "🥉"),

    /**
     * Coach plata - nivel intermedio
     */
    SILVER("Silver", "Intermediate level coach", 51, 150, "🥈"),

    /**
     * Coach oro - nivel avanzado
     */
    GOLD("Gold", "Advanced level coach", 151, 300, "🥇"),

    /**
     * Coach platino - nivel experto
     */
    PLATINUM("Platinum", "Expert level coach", 301, 500, "💎"),

    /**
     * Coach diamante - nivel maestro
     */
    DIAMOND("Diamond", "Master level coach", 501, 1000, "💠"),

    /**
     * Coach leyenda - el más alto nivel
     */
    LEGEND("Legend", "Legendary coach - highest level", 1001, Integer.MAX_VALUE, "👑");

    private final String displayName;
    private final String description;
    private final int minExperience;
    private final int maxExperience;
    private final String icon;

    CoachLevel(String displayName, String description, int minExperience, int maxExperience, String icon) {
        this.displayName = displayName;
        this.description = description;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getMinExperience() {
        return minExperience;
    }

    public int getMaxExperience() {
        return maxExperience;
    }

    public String getIcon() {
        return icon;
    }

    /**
     * Obtiene el nivel de coach basado en la experiencia
     */
    public static CoachLevel fromExperience(int experience) {
        for (CoachLevel level : values()) {
            if (experience >= level.minExperience && experience <= level.maxExperience) {
                return level;
            }
        }
        return ROOKIE; // Valor por defecto
    }

    /**
     * Obtiene el siguiente nivel de coach
     */
    public CoachLevel getNextLevel() {
        CoachLevel[] levels = values();
        int currentIndex = this.ordinal();
        if (currentIndex < levels.length - 1) {
            return levels[currentIndex + 1];
        }
        return this; // Ya está en el nivel máximo
    }

    /**
     * Obtiene el nivel anterior de coach
     */
    public CoachLevel getPreviousLevel() {
        CoachLevel[] levels = values();
        int currentIndex = this.ordinal();
        if (currentIndex > 0) {
            return levels[currentIndex - 1];
        }
        return this; // Ya está en el nivel mínimo
    }

    /**
     * Calcula la experiencia necesaria para el siguiente nivel
     */
    public int getExperienceToNextLevel(int currentExperience) {
        CoachLevel nextLevel = getNextLevel();
        if (nextLevel == this) {
            return 0; // Ya está en el nivel máximo
        }
        return Math.max(0, nextLevel.minExperience - currentExperience);
    }

    /**
     * Calcula el progreso hacia el siguiente nivel (0.0 a 1.0)
     */
    public double getProgressToNextLevel(int currentExperience) {
        if (this == LEGEND) {
            return 1.0; // Ya está en el nivel máximo
        }

        int experienceInCurrentLevel = currentExperience - this.minExperience;
        int experienceRangeCurrentLevel = this.maxExperience - this.minExperience + 1;

        return Math.min(1.0, Math.max(0.0, (double) experienceInCurrentLevel / experienceRangeCurrentLevel));
    }

    /**
     * Verifica si es un nivel alto (Gold o superior)
     */
    public boolean isHighLevel() {
        return this.ordinal() >= GOLD.ordinal();
    }

    /**
     * Verifica si es un nivel élite (Platinum o superior)
     */
    public boolean isEliteLevel() {
        return this.ordinal() >= PLATINUM.ordinal();
    }
}
