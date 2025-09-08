package com.impulse.domain.evidencia;

/**
 * Mapper para Evidencia <-> EvidenciaDTO. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class EvidenciaMapper {
    public static EvidenciaDTO toDTO(Evidencia evidencia) {
        if (evidencia == null) return null;
        EvidenciaDTO dto = new EvidenciaDTO();
        dto.setId(evidencia.getId());
        dto.setTipo(evidencia.getTipo());
        dto.setUrl(evidencia.getUrl());
        dto.setDescripcion(evidencia.getDescripcion());
        dto.setRetoId(evidencia.getReto() != null ? evidencia.getReto().getId() : null);
        dto.setValidado(evidencia.getValidado());
        dto.setCreatedAt(evidencia.getCreatedAt());
        dto.setUpdatedAt(evidencia.getUpdatedAt());
        dto.setDeletedAt(evidencia.getDeletedAt());
        dto.setCreatedBy(evidencia.getCreatedBy());
        dto.setUpdatedBy(evidencia.getUpdatedBy());
        return dto;
    }
    /**
     * Convierte un EvidenciaDTO a entidad Evidencia (sin relaciones, que debe gestionar el servicio).
     */
    public static Evidencia toEntity(EvidenciaDTO dto) {
        if (dto == null) return null;
        Evidencia evidencia = new Evidencia();
        evidencia.setId(dto.getId());
        evidencia.setTipo(dto.getTipo());
        evidencia.setUrl(dto.getUrl());
        evidencia.setDescripcion(dto.getDescripcion());
        evidencia.setValidado(dto.getValidado());
        // Relación reto debe ser gestionada por el servicio
        evidencia.setCreatedAt(dto.getCreatedAt());
        evidencia.setUpdatedAt(dto.getUpdatedAt());
        evidencia.setDeletedAt(dto.getDeletedAt());
        evidencia.setCreatedBy(dto.getCreatedBy());
        evidencia.setUpdatedBy(dto.getUpdatedBy());
        return evidencia;
    }
}
