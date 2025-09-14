package com.impulse.domain.enums;

/**
 * Niveles de dificultad para los retos
 */
public enum ChallengeDifficulty {
    VERY_EASY("Muy fácil", 1),
    EASY("Fácil", 2),
    MEDIUM("Medio", 3),
    HARD("Difícil", 4),
    VERY_HARD("Muy difícil", 5),
    EXTREME("Extremo", 6);

    private final String descripcion;
    private final int nivel;

    ChallengeDifficulty(String descripcion, int nivel) {
        this.descripcion = descripcion;
        this.nivel = nivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getNivel() {
        return nivel;
    }

    public static ChallengeDifficulty fromNivel(int nivel) {
        for (ChallengeDifficulty difficulty : values()) {
            if (difficulty.nivel == nivel) {
                return difficulty;
            }
        }
        return MEDIUM; // Por defecto
    }
}
