package com.impulse.domain.enums;

/**
 * Estados de las apelaciones de contenido
 */
public enum AppealStatus {
    PENDING("Pending", "Pendiente de revisión"),
    UNDER_REVIEW("Under Review", "En revisión"),
    ACCEPTED("Accepted", "Aceptada"),
    REJECTED("Rejected", "Rechazada");

    private final String code;
    private final String description;

    AppealStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    /**
     * Verifica si es un estado final
     */
    public boolean isFinal() {
        return this == ACCEPTED || this == REJECTED;
    }

    /**
     * Verifica si puede ser procesada
     */
    public boolean canBeProcessed() {
        return this == PENDING;
    }
}
