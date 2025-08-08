package com.impulse.domain.auditoria;

/**
 * Mapper para Auditoria <-> AuditoriaDTO. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class AuditoriaMapper {
    public static AuditoriaDTO toDTO(Auditoria auditoria) {
        if (auditoria == null) return null;
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.setId(auditoria.getId());
        dto.setAccion(auditoria.getAccion());
        dto.setUsuario(auditoria.getUsuario());
        dto.setResultado(auditoria.getResultado());
        dto.setDetalle(auditoria.getDetalle());
        dto.setCreatedAt(auditoria.getCreatedAt());
        return dto;
    }
    /**
     * Convierte un AuditoriaDTO a entidad Auditoria (sin relaciones, que debe gestionar el servicio).
     */
    public static Auditoria toEntity(AuditoriaDTO dto) {
        if (dto == null) return null;
        Auditoria auditoria = new Auditoria();
        auditoria.setId(dto.getId());
        auditoria.setAccion(dto.getAccion());
        auditoria.setUsuario(dto.getUsuario());
        auditoria.setResultado(dto.getResultado());
        auditoria.setDetalle(dto.getDetalle());
        auditoria.setCreatedAt(dto.getCreatedAt());
        return auditoria;
    }
}
