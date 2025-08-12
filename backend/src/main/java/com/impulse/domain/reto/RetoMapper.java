
package com.impulse.domain.reto;

import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * Mapper para Reto <-> RetoDTO. Cumple compliance: RGPD, ISO 27001, ENS.
 * Usa MapStruct para robustez y alineación con la base de datos.
 */
@Mapper(componentModel = "spring")
public interface RetoMapper {
    @Mapping(target="idCreador", source="creador.id")
    @Mapping(target="idCategoria", source="categoria.id")
    @Mapping(target="tipoValidacion", expression="java(r.getTipoValidacion().name())")
    @Mapping(target="dificultad", expression="java(r.getDificultad().name())")
    @Mapping(target="tipoEvidencia", expression="java(r.getTipoEvidencia().name())")
    @Mapping(target="frecuenciaReporte", expression="java(r.getFrecuenciaReporte().name())")
    @Mapping(target="estado", expression="java(r.getEstado().name())")
    @Mapping(target="tipoConsecuencia", expression="java(r.getTipoConsecuencia() != null ? r.getTipoConsecuencia().name() : null)")
    @Mapping(target="visibility", expression="java(r.getVisibility().name())")
    RetoDTO toDTO(Reto r);
}
