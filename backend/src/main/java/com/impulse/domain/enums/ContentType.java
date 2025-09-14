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

    /**
     * Convierte el código a ContentType
     */
    public static ContentType fromCode(String code) {
        for (ContentType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de contenido no válido: " + code);
    }

    /**
     * Verifica si es contenido creado por usuarios
     */
    public boolean isUserGeneratedContent() {
        return this == CHALLENGE || this == EVIDENCE || this == COMMENT;
    }
}
