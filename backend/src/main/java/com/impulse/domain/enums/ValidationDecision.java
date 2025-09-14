package com.impulse.domain.enums;

/**
 * Decisión de validación para evidencia
 */
public enum ValidationDecision {
    ACCEPTED("Aceptada"),
    REJECTED("Rechazada"),
    NEEDS_REVIEW("Necesita revisión"),
    PARTIALLY_ACCEPTED("Parcialmente aceptada"),
    PENDING_INFORMATION("Información pendiente");

    private final String descripcion;

    ValidationDecision(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
