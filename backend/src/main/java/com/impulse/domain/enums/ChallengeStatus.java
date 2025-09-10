package com.impulse.domain.enums;

/**
 * Estados del ciclo de vida de un Challenge
 */
public enum ChallengeStatus {
    DRAFT("Draft", "Borrador - En edición"),
    ACTIVE("Active", "Activo - Recibiendo evidencias"),
    COMPLETED("Completed", "Completado - Finalizado exitosamente"),
    CANCELLED("Cancelled", "Cancelado - Cancelado por el creador"),
    EXPIRED("Expired", "Expirado - Venció por tiempo");

    private final String code;
    private final String description;

    ChallengeStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    /**
     * Verifica si es un estado final (no puede cambiar)
     */
    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED || this == EXPIRED;
    }

    /**
     * Verifica si puede recibir evidencias
     */
    public boolean canReceiveEvidences() {
        return this == ACTIVE;
    }

    /**
     * Verifica si puede ser editado
     */
    public boolean canBeEdited() {
        return this == DRAFT;
    }

    /**
     * Verifica si está en progreso
     */
    public boolean isInProgress() {
        return this == ACTIVE;
    }
}
