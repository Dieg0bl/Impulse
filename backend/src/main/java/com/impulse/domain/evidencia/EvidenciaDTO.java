
package com.impulse.domain.evidencia;

import java.math.BigDecimal;
import java.time.Instant;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * DTO para Evidencia. Cumple compliance: RGPD, ISO 27001, ENS.
 * Exposición segura de campos alineados con la base de datos.
 */
public record EvidenciaDTO(
    Long id,
    Long retoId,
    Long usuarioId,
    String tipoEvidencia,
    String kind,
    String descripcion,
    String downloadUrl,
    String thumbnailUrl,
    String estadoValidacion,
    Instant fechaReporte,
    Instant fechaValidacion,
    Long validadorId,
    BigDecimal valorReportado,
    String unidadMedida,
    JsonNode metadatos
) {}
