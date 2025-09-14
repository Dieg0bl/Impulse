package com.impulse.domain.enums;

/**
 * Nivel de confianza en la validación
 */
public enum ConfidenceLevel {
    VERY_LOW("Muy baja", 0.0, 0.2),
    LOW("Baja", 0.2, 0.4),
    MEDIUM("Media", 0.4, 0.6),
    HIGH("Alta", 0.6, 0.8),
    VERY_HIGH("Muy alta", 0.8, 1.0);

    private final String descripcion;
    private final double minValue;
    private final double maxValue;

    ConfidenceLevel(String descripcion, double minValue, double maxValue) {
        this.descripcion = descripcion;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public static ConfidenceLevel fromScore(double score) {
        for (ConfidenceLevel level : values()) {
            if (score >= level.minValue && score <= level.maxValue) {
                return level;
            }
        }
        return MEDIUM; // Por defecto
    }
}
