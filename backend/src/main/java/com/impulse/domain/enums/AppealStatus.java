package com.impulse.domain.enums;

/**
 * Estados posibles de una apelación en el sistema de moderación
 */
public enum AppealStatus {
    /**
     * Apelación recién creada, pendiente de revisión
     */
    PENDING("Pending", "Appeal is waiting for review"),

    /**
     * Apelación está siendo revisada
     */
    UNDER_REVIEW("Under Review", "Appeal is currently being reviewed"),

    /**
     * Apelación fue aceptada - se revierte la acción original
     */
    APPROVED("Approved", "Appeal was approved - original action reversed"),

    /**
     * Apelación fue rechazada - se mantiene la acción original
     */
    DENIED("Denied", "Appeal was denied - original action stands"),

    /**
     * Apelación fue escalada para revisión de nivel superior
     */
    ESCALATED("Escalated", "Appeal has been escalated for higher-level review"),

    /**
     * Apelación fue parcialmente aceptada
     */
    PARTIALLY_APPROVED("Partially Approved", "Appeal was partially approved with modifications"),

    /**
     * Apelación fue cerrada por el usuario
     */
    WITHDRAWN("Withdrawn", "Appeal was withdrawn by the user"),

    /**
     * Apelación expiró sin revisión
     */
    EXPIRED("Expired", "Appeal expired without review");

    private final String displayName;
    private final String description;

    AppealStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Verifica si el estado indica que la apelación está activa
     */
    public boolean isActive() {
        return this == PENDING || this == UNDER_REVIEW || this == ESCALATED;
    }

    /**
     * Verifica si el estado indica que la apelación está finalizada
     */
    public boolean isFinal() {
        return this == APPROVED || this == DENIED || this == PARTIALLY_APPROVED ||
               this == WITHDRAWN || this == EXPIRED;
    }

    /**
     * Verifica si el estado indica una resolución favorable para el usuario
     */
    public boolean isFavorable() {
        return this == APPROVED || this == PARTIALLY_APPROVED;
    }

    /**
     * Verifica si el estado requiere atención del moderador
     */
    public boolean requiresModeratorAttention() {
        return this == PENDING || this == ESCALATED;
    }

    /**
     * Verifica si el usuario puede realizar acciones sobre esta apelación
     */
    public boolean allowsUserAction() {
        return this == PENDING || this == UNDER_REVIEW;
    }
}
