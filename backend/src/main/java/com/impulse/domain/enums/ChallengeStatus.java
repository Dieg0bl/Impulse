package com.impulse.domain.enums;

/**
 * Estados posibles de un reto
 */
public enum ChallengeStatus {
    DRAFT("Borrador"),
    PUBLISHED("Publicado"),
    ACTIVE("Activo"),
    PAUSED("Pausado"),
    COMPLETED("Completado"),
    CANCELLED("Cancelado"),
    ARCHIVED("Archivado"),
    UNDER_REVIEW("En revisión"),
    REJECTED("Rechazado");

    private final String descripcion;

    ChallengeStatus(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PUBLISHED;
    }

    public boolean canBeModified() {
        return this == DRAFT || this == UNDER_REVIEW;
    }

    public boolean canAcceptParticipants() {
        return this == ACTIVE || this == PUBLISHED;
    }
}
