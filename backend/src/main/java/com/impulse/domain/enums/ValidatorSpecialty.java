package com.impulse.domain.enums;

/**
 * Especialidades de validadores
 */
public enum ValidatorSpecialty {
    /**
     * Especialista en fitness y ejercicio
     */
    FITNESS("Fitness"),

    /**
     * Especialista en nutrición
     */
    NUTRITION("Nutrición"),

    /**
     * Especialista en bienestar mental
     */
    MENTAL_HEALTH("Salud Mental"),

    /**
     * Especialista en desarrollo personal
     */
    PERSONAL_DEVELOPMENT("Desarrollo Personal"),

    /**
     * Especialista en productividad
     */
    PRODUCTIVITY("Productividad"),

    /**
     * Especialista en finanzas personales
     */
    FINANCE("Finanzas"),

    /**
     * Especialista en educación
     */
    EDUCATION("Educación"),

    /**
     * Especialista en creatividad
     */
    CREATIVITY("Creatividad"),

    /**
     * Especialista en tecnología
     */
    TECHNOLOGY("Tecnología"),

    /**
     * Especialista en sostenibilidad
     */
    SUSTAINABILITY("Sostenibilidad"),

    /**
     * Validador general
     */
    GENERAL("General");

    private final String displayName;

    ValidatorSpecialty(String displayName) {
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
