package com.impulse.domain.enums;

/**
 * Estado de validación de evidencia
 */
public enum ValidationStatus {
    PENDING("Pendiente"),
    IN_PROGRESS("En proceso"),
    COMPLETED("Completada"),
    REJECTED("Rechazada"),
    EXPIRED("Expirada"),
    CANCELLED("Cancelada");

    private final String descripcion;

    ValidationStatus(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
