
package com.impulse.domain.evidencia;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.impulse.domain.reto.Reto;

/**
 * Mapper para Evidencia <-> EvidenciaDTO. Use explicit expressions that match the
 * current Evidencia entity shape to avoid compilation-time MapStruct errors.
 */
@Mapper(componentModel = "spring")
public interface EvidenciaMapper {

    @Mapping(target = "retoId", expression = "java(e.getReto() != null ? e.getReto().getId() : null)")
    // The entity stores the creator as a String (createdBy). We don't have a user id here,
    // so return null for usuarioId until the domain model is extended.
    @Mapping(target = "usuarioId", expression = "java((Long) null)")
    // No validador id in the entity currently; expose null for now.
    @Mapping(target = "validadorId", expression = "java((Long) null)")

    // Map basic string fields directly
    @Mapping(target = "tipoEvidencia", source = "tipo")
    @Mapping(target = "kind", expression = "java((String) null)")
    @Mapping(target = "descripcion", source = "descripcion")

    // Use the stored URL as the download URL. A SignedUrlService may be used later.
    @Mapping(target = "downloadUrl", source = "url")
    // No thumbnail stored on the entity yet.
    @Mapping(target = "thumbnailUrl", expression = "java((String) null)")

    @Mapping(target = "estadoValidacion", source = "validado")

    // Convert LocalDateTime (createdAt) to Instant safely
    @Mapping(target = "fechaReporte", expression = "java(e.getCreatedAt() != null ? e.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant() : null)")
    // fechaValidacion not present on the entity
    @Mapping(target = "fechaValidacion", expression = "java((java.time.Instant) null)")

    // Fields not present on the entity -> null
    @Mapping(target = "valorReportado", expression = "java((java.math.BigDecimal) null)")
    @Mapping(target = "unidadMedida", expression = "java((String) null)")
    @Mapping(target = "metadatos", expression = "java((com.fasterxml.jackson.databind.JsonNode) null)")
    @Mapping(target = "retoTitulo", expression = "java(e.getReto() != null ? e.getReto().getTitulo() : null)")
    @Mapping(target = "autorNombre", source = "createdBy")
    @Mapping(target = "contenido", expression = "java((String) null)")

    EvidenciaDTO toDTO(Evidencia e);

    // Map from DTO to entity. Create a Reto stub with id when retoId present.
    @Mapping(target = "tipo", source = "dto.tipoEvidencia")
    @Mapping(target = "url", source = "dto.downloadUrl")
    @Mapping(target = "descripcion", source = "dto.descripcion")
    @Mapping(target = "reto", expression = "java(dto.retoId() != null ? mapRetoIdToReto(dto.retoId()) : null)")
    @Mapping(target = "validado", source = "dto.estadoValidacion")
    @Mapping(target = "createdAt", expression = "java(dto.fechaReporte() != null ? instantToLocalDateTime(dto.fechaReporte()) : null)")
    // Fields not present in DTO or intentionally left unset
    @Mapping(target = "updatedAt", expression = "java((java.time.LocalDateTime) null)")
    @Mapping(target = "deletedAt", expression = "java((java.time.LocalDateTime) null)")
    @Mapping(target = "createdBy", expression = "java((String) null)")
    @Mapping(target = "updatedBy", expression = "java((String) null)")
    @Mapping(target = "comentario", source = "dto.descripcion")
    Evidencia toEntity(EvidenciaDTO dto);

    default Reto mapRetoIdToReto(Long id) {
        if (id == null) return null;
        Reto r = new Reto();
        r.setId(id);
        return r;
    }

    default LocalDateTime instantToLocalDateTime(Instant value) {
        return value == null ? null : LocalDateTime.ofInstant(value, ZoneId.systemDefault());
    }
}
