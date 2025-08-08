package com.impulse.domain.gamificacion;

/**
 * Mapper para Gamificacion <-> GamificacionDTO. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class GamificacionMapper {
    public static GamificacionDTO toDTO(Gamificacion gamificacion) {
        if (gamificacion == null) return null;
        GamificacionDTO dto = new GamificacionDTO();
        dto.setId(gamificacion.getId());
        dto.setTipo(gamificacion.getTipo());
        dto.setPuntos(gamificacion.getPuntos());
        dto.setUsuarioId(gamificacion.getUsuario() != null ? gamificacion.getUsuario().getId() : null);
        dto.setCreatedAt(gamificacion.getCreatedAt());
        dto.setUpdatedAt(gamificacion.getUpdatedAt());
        dto.setDeletedAt(gamificacion.getDeletedAt());
        dto.setCreatedBy(gamificacion.getCreatedBy());
        dto.setUpdatedBy(gamificacion.getUpdatedBy());
        return dto;
    }
    /**
     * Convierte un GamificacionDTO a entidad Gamificacion (sin relaciones, que debe gestionar el servicio).
     */
    public static Gamificacion toEntity(GamificacionDTO dto) {
        if (dto == null) return null;
        Gamificacion gamificacion = new Gamificacion();
        gamificacion.setId(dto.getId());
        gamificacion.setTipo(dto.getTipo());
        gamificacion.setPuntos(dto.getPuntos());
        gamificacion.setCanal(dto.getCanal()); // Si aplica
        // Relación usuario debe ser gestionada por el servicio
        gamificacion.setCreatedAt(dto.getCreatedAt());
        gamificacion.setUpdatedAt(dto.getUpdatedAt());
        gamificacion.setDeletedAt(dto.getDeletedAt());
        gamificacion.setCreatedBy(dto.getCreatedBy());
        gamificacion.setUpdatedBy(dto.getUpdatedBy());
        return gamificacion;
    }
}
