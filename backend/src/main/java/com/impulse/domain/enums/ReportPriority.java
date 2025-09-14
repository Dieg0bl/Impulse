package com.impulse.domain.enums;

/**
 * Prioridad del reporte
 */
public enum ReportPriority {
    LOW("Low", "Baja", 1),
    MEDIUM("Medium", "Media", 2),
    HIGH("High", "Alta", 3),
    URGENT("Urgent", "Urgente", 4);

    private final String code;
    private final String description;
    private final int level;

    ReportPriority(String code, String description, int level) {
        this.code = code;
        this.description = description;
        this.level = level;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
    public int getLevel() { return level; }

    public static ReportPriority fromReason(ReportReason reason) {
        if (reason.requiresImmediateReview()) {
            return URGENT;
        } else if (reason.isHighSeverity()) {
            return HIGH;
        } else if (reason.isContentRelated()) {
            return MEDIUM;
        } else {
            return LOW;
        }
    }
}
