package com.impulse.domain.enums;

/**
 * Tipo de validación de evidencia
 */
public enum ValidationType {
    /**
     * Validación automática
     */
    AUTOMATIC("Automática"),

    /**
     * Validación manual por validador
     */
    MANUAL("Manual"),

    /**
     * Validación por pares (peer review)
     */
    PEER_REVIEW("Revisión por Pares"),

    /**
     * Validación por experto
     */
    EXPERT_REVIEW("Revisión por Experto"),

    /**
     * Validación por IA
     */
    AI_VALIDATION("Validación por IA"),

    /**
     * Validación híbrida (automática + manual)
     */
    HYBRID("Híbrida"),

    /**
     * Validación por comunidad
     */
    COMMUNITY("Comunidad");

    private final String displayName;

    ValidationType(String displayName) {
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
