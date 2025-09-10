package com.impulse.domain.enums;

/**
 * Estados de las invitaciones para validadores
 */
public enum InvitationStatus {
    PENDING("Pending", "Pendiente de respuesta"),
    ACCEPTED("Accepted", "Aceptada"),
    DECLINED("Declined", "Rechazada"),
    EXPIRED("Expired", "Expirada");

    private final String code;
    private final String description;

    InvitationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    /**
     * Verifica si es un estado final (no puede cambiar)
     */
    public boolean isFinal() {
        return this == ACCEPTED || this == DECLINED || this == EXPIRED;
    }

    /**
     * Verifica si puede ser procesada
     */
    public boolean canBeProcessed() {
        return this == PENDING;
    }
}
