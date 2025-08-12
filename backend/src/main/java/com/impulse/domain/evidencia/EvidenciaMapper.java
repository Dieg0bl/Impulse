
package com.impulse.domain.evidencia;

import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * Mapper para Evidencia <-> EvidenciaDTO. Cumple compliance: RGPD, ISO 27001, ENS.
 * Usa MapStruct para robustez y alineación con la base de datos.
 */
@Mapper(componentModel = "spring", uses = SignedUrlService.class)
public interface EvidenciaMapper {
    @Mapping(target="retoId", source="reto.id")
    @Mapping(target="usuarioId", source="usuario.id")
    @Mapping(target="validadorId", source="validador.id")
    @Mapping(target="tipoEvidencia", expression="java(e.getTipoEvidencia().name())")
    @Mapping(target="kind", expression="java(e.getKind().name())")
    @Mapping(target="estadoValidacion", expression="java(e.getEstadoValidacion().name())")
    @Mapping(target="downloadUrl", expression="java(signedUrlService().signDownload(e.getArchivoUrl()))")
    @Mapping(target="thumbnailUrl", expression="java(signedUrlService().signDownload(e.getArchivoThumbnail()))")
    EvidenciaDTO toDTO(Evidencia e);

    @ObjectFactory default SignedUrlService signedUrlService(){ return null; }
}
