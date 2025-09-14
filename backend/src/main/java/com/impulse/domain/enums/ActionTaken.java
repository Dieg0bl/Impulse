package com.impulse.domain.enums;

/**
 * Acciones tomadas tras la moderación
 */
public enum ActionTaken {
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
