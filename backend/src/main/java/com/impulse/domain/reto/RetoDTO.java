
package com.impulse.domain.reto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO para Reto. Cumple compliance: RGPD, ISO 27001, ENS.
 * No expone datos sensibles ni relaciones completas.
 * Documenta todos los campos relevantes y flags de privacidad.
 */
public record RetoDTO(
	Long id,
	Long idCreador,
	Long idCategoria,
	String titulo,
	String descripcion,
	Instant fechaInicio,
	Instant fechaFin,
	String tipoValidacion,
	String dificultad,
	Boolean esPublico,
	Boolean requiereEvidencia,
	String tipoEvidencia,
	String frecuenciaReporte,
	String metaObjetivo,
	String unidadMedida,
	BigDecimal valorObjetivo,
	String estado,
	String publicSlug,
	Integer slaHorasValidacion,
	String tipoConsecuencia,
	Boolean esPlantilla,
	String visibility,
	Instant updatedAt
) {}
