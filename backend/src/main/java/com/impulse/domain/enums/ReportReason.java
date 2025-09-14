package com.impulse.domain.enums;

/**
 * Razones para reportar contenido o usuarios
 */
public enum ReportReason {
    SPAM("Spam", "Contenido spam o promocional no deseado"),
    HATE_SPEECH("Hate Speech", "Discurso de odio o discriminatorio"),
    HARASSMENT("Harassment", "Acoso o intimidación"),
    FALSE_INFO("False Information", "Información falsa o engañosa"),
    COPYRIGHT("Copyright", "Violación de derechos de autor"),
    ADULT_CONTENT("Adult Content", "Contenido para adultos inapropiado"),
    VIOLENCE("Violence", "Contenido violento o que promueve la violencia"),
    OTHER("Other", "Otro motivo");

    private final String code;
    private final String description;

    ReportReason(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public boolean requiresImmediateReview() {
        return this == HATE_SPEECH || this == HARASSMENT || this == ADULT_CONTENT || this == VIOLENCE;
    }

    public boolean isHighSeverity() {
        return this == HATE_SPEECH || this == HARASSMENT || this == FALSE_INFO || this == VIOLENCE;
    }

    public boolean isContentRelated() {
        return this == SPAM || this == FALSE_INFO || this == COPYRIGHT || this == ADULT_CONTENT;
    }
}
