package com.impulse.domain.enums;

/**
 * Estado de un validador en el sistema
 */
public enum ValidatorStatus {
    /**
     * Validador activo y disponible
     */
    ACTIVE("Activo"),

    /**
     * Validador inactivo temporalmente
     */
    INACTIVE("Inactivo"),

    /**
     * Validador suspendido
     */
    SUSPENDED("Suspendido"),

    /**
     * Validador en proceso de verificación
     */
    PENDING_VERIFICATION("Pendiente de Verificación"),

    /**
     * Validador bloqueado
     */
    BLOCKED("Bloqueado"),

    /**
     * Validador retirado del sistema
     */
    RETIRED("Retirado");

    private final String displayName;

    ValidatorStatus(String displayName) {
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
