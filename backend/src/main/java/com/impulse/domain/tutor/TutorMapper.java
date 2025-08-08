package com.impulse.domain.tutor;

/**
 * Mapper para convertir entre Tutor y TutorDTO.
 * Cumple compliance: nunca expone datos sensibles.
 */
import java.util.List;
// import java.util.stream.Collectors; // Removed, not needed with Java 17+

public class TutorMapper {
    private TutorMapper() { throw new UnsupportedOperationException("Clase utilitaria"); }
    /**
     * Convierte una entidad Tutor a TutorDTO.
     * No expone datos sensibles ni la lista completa de usuarios.
     */
    public static TutorDTO toDTO(Tutor entity) {
        if (entity == null) return null;
        TutorDTO dto = new TutorDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setNombre(entity.getNombre());
        // Solo IDs de usuarios
        if (entity.getUsuarios() != null) {
            List<Long> ids = entity.getUsuarios().stream()
                .map(u -> u != null ? u.getId() : null)
                .toList();
            dto.setUsuariosIds(ids);
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDeletedAt(entity.getDeletedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }

    /**
     * Convierte un TutorDTO a entidad Tutor (sin usuarios, que debe gestionar el servicio).
     */
    public static Tutor toEntity(TutorDTO dto) {
        if (dto == null) return null;
        Tutor entity = new Tutor();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setNombre(dto.getNombre());
        // La relación usuarios debe ser gestionada por el servicio
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setDeletedAt(dto.getDeletedAt());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getUpdatedBy());
        return entity;
    }
}
