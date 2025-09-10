package com.impulse.domain.enums;

/**
 * Tipos de notificación del sistema
 */
public enum NotificationType {
    // Notificaciones de Challenge
    CHALLENGE_CREATED("challenge.created", "Nuevo Challenge Creado", 
        "Se ha creado un nuevo challenge: {challengeTitle}", NotificationPriority.LOW),
    CHALLENGE_STARTED("challenge.started", "Challenge Iniciado", 
        "El challenge '{challengeTitle}' ha comenzado", NotificationPriority.MEDIUM),
    CHALLENGE_COMPLETED("challenge.completed", "Challenge Completado", 
        "El challenge '{challengeTitle}' ha sido completado", NotificationPriority.MEDIUM),
    CHALLENGE_EXPIRED("challenge.expired", "Challenge Expirado", 
        "El challenge '{challengeTitle}' ha expirado", NotificationPriority.HIGH),
    
    // Notificaciones de Evidence
    EVIDENCE_SUBMITTED("evidence.submitted", "Nueva Evidencia Enviada", 
        "Nueva evidencia para el challenge '{challengeTitle}'", NotificationPriority.MEDIUM),
    EVIDENCE_VALIDATED("evidence.validated", "Evidencia Validada", 
        "Tu evidencia ha sido validada en '{challengeTitle}'", NotificationPriority.HIGH),
    EVIDENCE_REJECTED("evidence.rejected", "Evidencia Rechazada", 
        "Tu evidencia fue rechazada en '{challengeTitle}'. Motivo: {reason}", NotificationPriority.HIGH),
    
    // Notificaciones de Coach
    COACH_INVITATION("coach.invitation", "Invitación de Coach", 
        "Has sido invitado como coach para '{challengeTitle}'", NotificationPriority.HIGH),
    COACH_ACCEPTED("coach.accepted", "Coach Aceptado", 
        "El coach {coachName} ha aceptado tu invitación", NotificationPriority.MEDIUM),
    COACH_FEEDBACK("coach.feedback", "Nuevo Feedback de Coach", 
        "Has recibido feedback del coach en '{challengeTitle}'", NotificationPriority.MEDIUM),
    
    // Notificaciones de Sistema
    SYSTEM_MAINTENANCE("system.maintenance", "Mantenimiento del Sistema", 
        "El sistema entrará en mantenimiento el {date}", NotificationPriority.HIGH),
    SYSTEM_UPDATE("system.update", "Actualización del Sistema", 
        "Nueva versión disponible con mejoras y correcciones", NotificationPriority.LOW),
    
    // Notificaciones de Moderación
    CONTENT_REPORTED("moderation.reported", "Contenido Reportado", 
        "Se ha reportado contenido que requiere tu revisión", NotificationPriority.HIGH),
    CONTENT_MODERATED("moderation.actioned", "Contenido Moderado", 
        "Tu contenido ha sido moderado. Estado: {status}", NotificationPriority.HIGH),
    
    // Notificaciones de Logros
    ACHIEVEMENT_UNLOCKED("achievement.unlocked", "Logro Desbloqueado", 
        "¡Felicidades! Has desbloqueado: {achievementName}", NotificationPriority.MEDIUM),
    LEVEL_UP("level.up", "Subida de Nivel", 
        "¡Has alcanzado el nivel {level}!", NotificationPriority.MEDIUM);

    private final String code;
    private final String title;
    private final String templateMessage;
    private final NotificationPriority priority;

    NotificationType(String code, String title, String templateMessage, NotificationPriority priority) {
        this.code = code;
        this.title = title;
        this.templateMessage = templateMessage;
        this.priority = priority;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getTemplateMessage() { return templateMessage; }
    public NotificationPriority getPriority() { return priority; }

    /**
     * Verifica si es notificación de tiempo real
     */
    public boolean isRealTime() {
        return priority == NotificationPriority.HIGH;
    }

    /**
     * Verifica si requiere acción del usuario
     */
    public boolean requiresAction() {
        return this == COACH_INVITATION || 
               this == CONTENT_REPORTED || 
               this == EVIDENCE_REJECTED;
    }

    /**
     * Verifica si es notificación del sistema
     */
    public boolean isSystemNotification() {
        return code.startsWith("system.");
    }

    /**
     * Verifica si es notificación de moderación
     */
    public boolean isModerationNotification() {
        return code.startsWith("moderation.");
    }

    /**
     * Prioridad de las notificaciones
     */
    public enum NotificationPriority {
        LOW(1, "Baja"),
        MEDIUM(2, "Media"),
        HIGH(3, "Alta");

        private final int level;
        private final String description;

        NotificationPriority(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() { return level; }
        public String getDescription() { return description; }
    }
}
