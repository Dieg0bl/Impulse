package com.impulse.domain.reto;

// ...existing code...
import java.util.stream.Collectors;

/**
 * Mapper para Reto <-> RetoDTO. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class RetoMapper {
    // Constructor privado para evitar instanciación
    private RetoMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static RetoDTO toDTO(Reto reto) {
        if (reto == null) return null;
        RetoDTO dto = new RetoDTO();
        dto.setId(reto.getId());
        dto.setTitulo(reto.getTitulo());
        dto.setDescripcion(reto.getDescripcion());
        dto.setFechaInicio(reto.getFechaInicio());
        dto.setFechaFin(reto.getFechaFin());
        dto.setEstado(reto.getEstado());
        dto.setUsuarioId(null);
        dto.setTutoresIds(reto.getTutores() != null ? reto.getTutores().stream().map(t -> t.getId()).collect(Collectors.toList()) : null);
        dto.setEvidenciasIds(reto.getEvidencias() != null ? reto.getEvidencias().stream().map(e -> e.getId()).collect(Collectors.toList()) : null);
        dto.setCreatedAt(reto.getCreatedAt());
        dto.setUpdatedAt(reto.getUpdatedAt());
        dto.setDeletedAt(reto.getDeletedAt());
        dto.setCreatedBy(reto.getCreatedBy());
        dto.setUpdatedBy(reto.getUpdatedBy());
        dto.setDeletedBy(reto.getDeletedBy());
        return dto;
    }
    /**
     * Convierte un RetoDTO a entidad Reto (sin relaciones, que debe gestionar el servicio).
     */
    public static Reto toEntity(RetoDTO dto) {
        if (dto == null) return null;
        Reto reto = new Reto();
        reto.setId(dto.getId());
        reto.setTitulo(dto.getTitulo());
        reto.setDescripcion(dto.getDescripcion());
        reto.setFechaInicio(dto.getFechaInicio());
        reto.setFechaFin(dto.getFechaFin());
        reto.setEstado(dto.getEstado());
        // Relaciones usuario, tutores y evidencias deben ser gestionadas por el servicio
        reto.setCreatedAt(dto.getCreatedAt());
        reto.setUpdatedAt(dto.getUpdatedAt());
        reto.setDeletedAt(dto.getDeletedAt());
        reto.setCreatedBy(dto.getCreatedBy());
        reto.setUpdatedBy(dto.getUpdatedBy());
        reto.setDeletedBy(dto.getDeletedBy());
        return reto;
    }
}
