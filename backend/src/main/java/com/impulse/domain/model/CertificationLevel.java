package com.impulse.domain.model;

/**
 * Nivel de certificación de validadores
 */
public enum CertificationLevel {
    /**
     * Validador novato
     */
    NOVICE("Novato"),

    /**
     * Validador básico
     */
    BASIC("Básico"),

    /**
     * Validador intermedio
     */
    INTERMEDIATE("Intermedio"),

    /**
     * Validador avanzado
     */
    ADVANCED("Avanzado"),

    /**
     * Validador experto
     */
    EXPERT("Experto"),

    /**
     * Validador maestro
     */
    MASTER("Maestro"),

    /**
     * Validador certificado profesionalmente
     */
    PROFESSIONAL("Profesional");

    private final String displayName;

    CertificationLevel(String displayName) {
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
