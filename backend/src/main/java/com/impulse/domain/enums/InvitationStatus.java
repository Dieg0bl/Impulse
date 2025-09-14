package com.impulse.domain.enums;

/**
 * Estados posibles de una invitación en el sistema
 */
public enum InvitationStatus {
    /**
     * Invitación enviada, pendiente de respuesta
     */
    PENDING("Pending", "Invitation sent, waiting for response"),

    /**
     * Invitación aceptada
     */
    ACCEPTED("Accepted", "Invitation has been accepted"),

    /**
     * Invitación rechazada
     */
    DECLINED("Declined", "Invitation has been declined"),

    /**
     * Invitación expiró sin respuesta
     */
    EXPIRED("Expired", "Invitation expired without response"),

    /**
     * Invitación cancelada por el emisor
     */
    CANCELLED("Cancelled", "Invitation was cancelled by sender"),

    /**
     * Invitación revocada (después de ser aceptada)
     */
    REVOKED("Revoked", "Invitation was revoked after acceptance");

    private final String displayName;
    private final String description;

    InvitationStatus(String displayName, String description) {
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
     * Verifica si la invitación está activa (puede ser respondida)
     */
    public boolean isActive() {
        return this == PENDING;
    }

    /**
     * Verifica si la invitación está finalizada
     */
    public boolean isFinal() {
        return this == ACCEPTED || this == DECLINED || this == EXPIRED ||
               this == CANCELLED || this == REVOKED;
    }

    /**
     * Verifica si la invitación fue exitosa (aceptada)
     */
    public boolean isSuccessful() {
        return this == ACCEPTED;
    }

    /**
     * Verifica si el receptor puede responder a la invitación
     */
    public boolean canBeResponded() {
        return this == PENDING;
    }

    /**
     * Verifica si el emisor puede cancelar la invitación
     */
    public boolean canBeCancelled() {
        return this == PENDING;
    }

    /**
     * Verifica si se puede enviar una nueva invitación
     */
    public boolean allowsNewInvitation() {
        return this == DECLINED || this == EXPIRED || this == CANCELLED;
    }
}
