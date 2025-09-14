package com.impulse.domain.model;

/**
 * Estado de asignación de validador
 */
public enum AssignmentStatus {
    /**
     * Asignación pendiente
     */
    PENDING("Pendiente"),

    /**
     * Asignación aceptada
     */
    ACCEPTED("Aceptada"),

    /**
     * Asignación en progreso
     */
    IN_PROGRESS("En Progreso"),

    /**
     * Asignación completada
     */
    COMPLETED("Completada"),

    /**
     * Asignación rechazada
     */
    REJECTED("Rechazada"),

    /**
     * Asignación cancelada
     */
    CANCELLED("Cancelada"),

    /**
     * Asignación vencida
     */
    EXPIRED("Vencida");

    private final String displayName;

    AssignmentStatus(String displayName) {
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
