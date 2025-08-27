
package com.impulse.domain.reto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.impulse.domain.usuario.Usuario;

/**
 * Mapper para Reto <-> RetoDTO. Adds a helper to convert LocalDateTime to Instant
 * so MapStruct can generate the mapping code.
 */
@Mapper(componentModel = "spring")
public interface RetoMapper {
    @Mapping(target = "idCreador", source = "usuario.id")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "fechaInicio", source = "fechaInicio")
    @Mapping(target = "fechaFin", source = "fechaFin")
    // For fields not present on the entity, return null in the DTO
    @Mapping(target = "tipoValidacion", expression = "java((String) null)")
    @Mapping(target = "dificultad", expression = "java((String) null)")
    @Mapping(target = "tipoEvidencia", expression = "java((String) null)")
    @Mapping(target = "frecuenciaReporte", expression = "java((String) null)")
    @Mapping(target = "idCategoria", expression = "java((Long) null)")
    @Mapping(target = "esPublico", expression = "java((Boolean) null)")
    @Mapping(target = "requiereEvidencia", expression = "java((Boolean) null)")
    @Mapping(target = "metaObjetivo", expression = "java((String) null)")
    @Mapping(target = "unidadMedida", expression = "java((String) null)")
    @Mapping(target = "valorObjetivo", expression = "java((java.math.BigDecimal) null)")
    @Mapping(target = "progreso", expression = "java((Integer) null)")
    @Mapping(target = "validadores", expression = "java((java.util.List<Long>) null)")
    @Mapping(target = "reportes", expression = "java((java.util.List) null)")
    @Mapping(target = "recompensas", expression = "java((RetoDTO.Recompensas) null)")
    @Mapping(target = "configuracion", expression = "java((RetoDTO.Configuracion) null)")
    @Mapping(target = "publicSlug", expression = "java((String) null)")
    @Mapping(target = "slaHorasValidacion", expression = "java((Integer) null)")
    @Mapping(target = "tipoConsecuencia", expression = "java((String) null)")
    @Mapping(target = "esPlantilla", expression = "java((Boolean) null)")
    @Mapping(target = "visibility", expression = "java((String) null)")
    @Mapping(target = "fechaCreacion", expression = "java(map(r.getCreatedAt()))")
    @Mapping(target = "fechaActualizacion", expression = "java(map(r.getUpdatedAt()))")
    RetoDTO toDTO(Reto r);

    // Convert LocalDateTime -> Instant for mapping
    default Instant map(LocalDateTime value) {
        return value == null ? null : value.atZone(ZoneId.systemDefault()).toInstant();
    }

    // Map from DTO to entity. Only map existing entity fields; others are ignored.
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "fechaLimite", expression = "java((java.time.LocalDateTime) null)")
    @Mapping(target = "createdAt", expression = "java(dto.fechaCreacion() != null ? instantToLocalDateTime(dto.fechaCreacion()) : null)")
    @Mapping(target = "usuario", expression = "java(dto.idCreador() != null ? mapUsuarioId(dto.idCreador()) : null)")
    @Mapping(target = "tutores", expression = "java((java.util.List<com.impulse.domain.tutor.Tutor>) null)")
    @Mapping(target = "evidencias", expression = "java((java.util.List<com.impulse.domain.evidencia.Evidencia>) null)")
    @Mapping(target = "deletedAt", expression = "java((java.time.LocalDateTime) null)")
    @Mapping(target = "createdBy", expression = "java((String) null)")
    @Mapping(target = "updatedBy", expression = "java((String) null)")
    @Mapping(target = "deletedBy", expression = "java((String) null)")
    Reto toEntity(RetoDTO dto);

    default LocalDateTime instantToLocalDateTime(Instant value) {
        return value == null ? null : LocalDateTime.ofInstant(value, ZoneId.systemDefault());
    }

    default Usuario mapUsuarioId(Long id) {
        if (id == null) return null;
        Usuario u = new Usuario();
        u.setId(id);
        return u;
    }
}
