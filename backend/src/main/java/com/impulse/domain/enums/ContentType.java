package com.impulse.domain.enums;

/**
 * Tipos de contenido que pueden ser reportados
 */
public enum ContentType {
    CHALLENGE("Challenge", "Reto"),
    EVIDENCE("Evidence", "Evidencia"),
    USER("User", "Usuario"),
    COMMENT("Comment", "Comentario");

    private final String code;
    private final String description;

    ContentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
}

/**
 * Razones para reportar contenido
 */
enum ReportReason {
    SPAM("Spam", "Contenido spam o promocional no deseado"),
    HATE_SPEECH("Hate Speech", "Discurso de odio o discriminatorio"),
    HARASSMENT("Harassment", "Acoso o intimidación"),
    FALSE_INFO("False Information", "Información falsa o engañosa"),
    COPYRIGHT("Copyright", "Violación de derechos de autor"),
    ADULT_CONTENT("Adult Content", "Contenido para adultos inapropiado"),
    OTHER("Other", "Otro motivo");

    private final String code;
    private final String description;

    ReportReason(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
}

/**
 * Estados del reporte de moderación
 */
enum ReportStatus {
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
}

/**
 * Prioridad del reporte
 */
enum ReportPriority {
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
}

/**
 * Acciones tomadas tras la moderación
 */
enum ActionTaken {
    NONE("None", "Ninguna acción tomada"),
    WARNING("Warning", "Advertencia al usuario"),
    CONTENT_REMOVED("Content Removed", "Contenido eliminado"),
    USER_SUSPENDED("User Suspended", "Usuario suspendido temporalmente"),
    USER_BANNED("User Banned", "Usuario baneado permanentemente");

    private final String code;
    private final String description;

    ActionTaken(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    /**
     * Verifica si es una acción disciplinaria
     */
    public boolean isDisciplinary() {
        return this != NONE;
    }

    /**
     * Verifica si es una acción severa
     */
    public boolean isSevere() {
        return this == USER_SUSPENDED || this == USER_BANNED;
    }
}
