package com.impulse.domain.enums;

/**
 * Estado de participación en un challenge
 */
public enum ParticipationStatus {
    /**
     * Participación registrada pero no iniciada
     */
    REGISTERED("Registrado"),

    /**
     * Participación en progreso
     */
    IN_PROGRESS("En Progreso"),

    /**
     * Participación completada exitosamente
     */
    COMPLETED("Completado"),

    /**
     * Participación abandonada
     */
    ABANDONED("Abandonado"),

    /**
     * Participación suspendida
     */
    SUSPENDED("Suspendido"),

    /**
     * Participación cancelada
     */
    CANCELLED("Cancelado");

    private final String displayName;

    ParticipationStatus(String displayName) {
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
