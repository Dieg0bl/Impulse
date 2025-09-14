package com.impulse.domain.enums;

/**
 * Acción recomendada tras la validación
 */
public enum RecommendedAction {
    APPROVE("Aprobar"),
    REJECT("Rechazar"),
    REQUEST_MORE_INFO("Solicitar más información"),
    MANUAL_REVIEW("Revisión manual"),
    APPROVE_WITH_CONDITIONS("Aprobar con condiciones"),
    ESCALATE("Escalar"),
    RESUBMIT("Re-enviar"),
    NONE("Ninguna");

    private final String descripcion;

    RecommendedAction(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
