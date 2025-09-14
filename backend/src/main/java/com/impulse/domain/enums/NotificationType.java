package com.impulse.domain.enums;

/**
 * Tipos de notificaciones en el sistema
 */
public enum NotificationType {
    /**
     * Notificación de sistema general
     */
    SYSTEM("System", "System notification", "🔔"),

    /**
     * Notificación de nuevo reto
     */
    NEW_CHALLENGE("New Challenge", "New challenge notification", "🎯"),

    /**
     * Notificación de reto completado
     */
    CHALLENGE_COMPLETED("Challenge Completed", "Challenge completion notification", "✅"),

    /**
     * Notificación de nuevo seguidor
     */
    NEW_FOLLOWER("New Follower", "New follower notification", "👥"),

    /**
     * Notificación de nuevo comentario
     */
    NEW_COMMENT("New Comment", "New comment notification", "💬"),

    /**
     * Notificación de nuevo me gusta
     */
    NEW_LIKE("New Like", "New like notification", "❤️"),

    /**
     * Notificación de logro desbloqueado
     */
    ACHIEVEMENT("Achievement", "Achievement unlocked notification", "🏆"),

    /**
     * Notificación de mensaje directo
     */
    DIRECT_MESSAGE("Direct Message", "Direct message notification", "📩"),

    /**
     * Notificación de recordatorio
     */
    REMINDER("Reminder", "Reminder notification", "⏰"),

    /**
     * Notificación de sesión de coaching
     */
    COACHING_SESSION("Coaching Session", "Coaching session notification", "🎓"),

    /**
     * Notificación de pago/facturación
     */
    BILLING("Billing", "Billing notification", "💳"),

    /**
     * Notificación de seguridad
     */
    SECURITY("Security", "Security notification", "🔒"),

    /**
     * Notificación de moderación
     */
    MODERATION("Moderation", "Moderation notification", "⚠️"),

    /**
     * Notificación de promoción o marketing
     */
    PROMOTIONAL("Promotional", "Promotional notification", "🎁");

    private final String displayName;
    private final String description;
    private final String icon;

    NotificationType(String displayName, String description, String icon) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    /**
     * Verifica si es una notificación crítica que requiere atención inmediata
     */
    public boolean isCritical() {
        return this == SECURITY || this == MODERATION || this == BILLING;
    }

    /**
     * Verifica si es una notificación social (interacciones entre usuarios)
     */
    public boolean isSocial() {
        return this == NEW_FOLLOWER || this == NEW_COMMENT || this == NEW_LIKE ||
               this == DIRECT_MESSAGE;
    }

    /**
     * Verifica si es una notificación relacionada con retos
     */
    public boolean isChallengeRelated() {
        return this == NEW_CHALLENGE || this == CHALLENGE_COMPLETED || this == ACHIEVEMENT;
    }

    /**
     * Obtiene la prioridad de la notificación
     */
    public NotificationPriority getPriority() {
        if (isCritical()) {
            return NotificationPriority.HIGH;
        } else if (isSocial() || isChallengeRelated()) {
            return NotificationPriority.NORMAL;
        } else {
            return NotificationPriority.LOW;
        }
    }

    /**
     * Enum interno para prioridad de notificaciones
     */
    public enum NotificationPriority {
        LOW, NORMAL, HIGH
    }
}
