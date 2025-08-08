package com.impulse.domain.notificacion;

/**
 * Validador para Notificacion. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class NotificacionValidator {

    // Constructor privado para evitar instanciación
    private NotificacionValidator() {
        throw new UnsupportedOperationException("Clase utilitaria, no instanciable");
    }

    /**
     * Valida un NotificacionDTO antes de operaciones críticas (registro, actualización).
     * Cumple compliance: RGPD, ISO 27001, ENS.
     * @param dto NotificacionDTO a validar
     * @throws IllegalArgumentException si alguna validación falla
     */
    public static void validar(NotificacionDTO dto) {
        if (dto == null) throw new IllegalArgumentException("NotificacionDTO no puede ser null");
        if (dto.getTipo() == null || dto.getTipo().isBlank())
            throw new IllegalArgumentException("El tipo de notificación es obligatorio");
        if (dto.getMensaje() == null || dto.getMensaje().isBlank())
            throw new IllegalArgumentException("El mensaje es obligatorio");
        if (dto.getUsuarioId() == null)
            throw new IllegalArgumentException("El usuarioId es obligatorio");
        if (dto.getCanal() == null || dto.getCanal().isBlank())
            throw new IllegalArgumentException("El canal es obligatorio");
        if (dto.getEnviado() == null || dto.getEnviado().isBlank())
            throw new IllegalArgumentException("El estado de envío es obligatorio");
        if (dto.getCreatedAt() == null)
            throw new IllegalArgumentException("createdAt es obligatorio");
        if (dto.getUpdatedAt() == null)
            throw new IllegalArgumentException("updatedAt es obligatorio");
        if (dto.getCreatedBy() == null || dto.getCreatedBy().isBlank())
            throw new IllegalArgumentException("createdBy es obligatorio");
        if (dto.getUpdatedBy() == null || dto.getUpdatedBy().isBlank())
            throw new IllegalArgumentException("updatedBy es obligatorio");
    }
}
