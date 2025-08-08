package com.impulse.domain.notificacion;


/**
 * Mapper para Notificacion <-> NotificacionDTO. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class NotificacionMapper {
    private NotificacionMapper() {}

    public static NotificacionDTO toDTO(Notificacion notificacion) {
        if (notificacion == null) return null;
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(notificacion.getId());
        dto.setTipo(notificacion.getTipo());
        dto.setMensaje(notificacion.getMensaje());
        dto.setUsuarioId(null);
        dto.setCanal(notificacion.getCanal());
        dto.setEnviado(notificacion.getEnviado());
        dto.setCreatedAt(notificacion.getCreatedAt());
        dto.setUpdatedAt(notificacion.getUpdatedAt());
        dto.setDeletedAt(notificacion.getDeletedAt());
        dto.setCreatedBy(notificacion.getCreatedBy());
        dto.setUpdatedBy(notificacion.getUpdatedBy());
        return dto;
    }
    /**
     * Convierte un NotificacionDTO a entidad Notificacion (sin relaciones, que debe gestionar el servicio).
     */
    public static Notificacion toEntity(NotificacionDTO dto) {
        if (dto == null) return null;
        Notificacion notificacion = new Notificacion();
        notificacion.setId(dto.getId());
        notificacion.setTipo(dto.getTipo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setCanal(dto.getCanal());
        notificacion.setEnviado(dto.getEnviado());
        // Relación usuario debe ser gestionada por el servicio
        notificacion.setCreatedAt(dto.getCreatedAt());
        notificacion.setUpdatedAt(dto.getUpdatedAt());
        notificacion.setDeletedAt(dto.getDeletedAt());
        notificacion.setCreatedBy(dto.getCreatedBy());
        notificacion.setUpdatedBy(dto.getUpdatedBy());
        return notificacion;
    }
}
