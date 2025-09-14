package com.impulse.domain.enums;

/**
 * Estado de una evidencia
 */
public enum EvidenceStatus {
    /**
     * Evidencia creada pero no enviada
     */
    DRAFT("Borrador"),

    /**
     * Evidencia enviada y pendiente de validación
     */
    PENDING_VALIDATION("Pendiente de Validación"),

    /**
     * Evidencia en proceso de validación
     */
    UNDER_REVIEW("En Revisión"),

    /**
     * Evidencia validada y aprobada
     */
    VALIDATED("Validada"),

    /**
     * Evidencia rechazada
     */
    REJECTED("Rechazada"),

    /**
     * Evidencia requiere revisión adicional
     */
    NEEDS_REVISION("Requiere Revisión"),

    /**
     * Evidencia suspendida por problemas
     */
    SUSPENDED("Suspendida"),

    /**
     * Evidencia archivada
     */
    ARCHIVED("Archivada");

    private final String displayName;

    EvidenceStatus(String displayName) {
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
