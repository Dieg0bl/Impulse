package com.impulse.domain.enums;

/**
 * Estados del reporte de moderación
 */
public enum ReportStatus {
    PENDING("Pending", "Pendiente de revisión"),
    UNDER_REVIEW("Under Review", "En revisión"),
    RESOLVED("Resolved", "Resuelto"),
    DISMISSED("Dismissed", "Desestimado");

    private final String code;
    private final String description;

    ReportStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public boolean isActive() {
        return this == PENDING || this == UNDER_REVIEW;
    }

    public boolean isCompleted() {
        return this == RESOLVED || this == DISMISSED;
    }
}
