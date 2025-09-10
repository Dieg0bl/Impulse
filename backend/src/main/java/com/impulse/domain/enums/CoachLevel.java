package com.impulse.domain.enums;

/**
 * Niveles de Coach en el marketplace
 * - STARTER: Coach nuevo (0-49 puntos)
 * - RISING: Coach intermedio (50-79 puntos)
 * - CHAMPION: Coach experto (80-100 puntos)
 */
public enum CoachLevel {
    STARTER("Starter", 0, 49, 0.15),     // 15% comisión
    RISING("Rising", 50, 79, 0.12),      // 12% comisión  
    CHAMPION("Champion", 80, 100, 0.10); // 10% comisión

    private final String displayName;
    private final int minScore;
    private final int maxScore;
    private final double commissionRate;

    CoachLevel(String displayName, int minScore, int maxScore, double commissionRate) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.commissionRate = commissionRate;
    }

    public String getDisplayName() { return displayName; }
    public int getMinScore() { return minScore; }
    public int getMaxScore() { return maxScore; }
    public double getCommissionRate() { return commissionRate; }

    /**
     * Obtiene el nivel basado en el score
     */
    public static CoachLevel fromScore(int score) {
        if (score >= CHAMPION.minScore) return CHAMPION;
        if (score >= RISING.minScore) return RISING;
        return STARTER;
    }

    /**
     * Verifica si puede ascender al siguiente nivel
     */
    public boolean canUpgrade(int currentScore) {
        return switch (this) {
            case STARTER -> currentScore >= RISING.minScore;
            case RISING -> currentScore >= CHAMPION.minScore;
            case CHAMPION -> false; // Ya es el máximo nivel
        };
    }

    /**
     * Obtiene el siguiente nivel
     */
    public CoachLevel getNextLevel() {
        return switch (this) {
            case STARTER -> RISING;
            case RISING -> CHAMPION;
            case CHAMPION -> CHAMPION; // Ya es el máximo
        };
    }
}
