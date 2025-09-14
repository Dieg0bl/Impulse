package com.impulse.domain.enums;

/**
 * Prioridad de validación
 */
public enum ValidationPriority {
    /**
     * Prioridad baja
     */
    LOW("Baja"),

    /**
     * Prioridad normal
     */
    NORMAL("Normal"),

    /**
     * Prioridad alta
     */
    HIGH("Alta"),

    /**
     * Prioridad crítica
     */
    CRITICAL("Crítica"),

    /**
     * Prioridad urgente
     */
    URGENT("Urgente");

    private final String displayName;

    ValidationPriority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
