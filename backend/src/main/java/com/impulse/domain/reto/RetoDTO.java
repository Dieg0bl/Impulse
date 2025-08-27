
package com.impulse.domain.reto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
	Integer progreso,
	List<Long> validadores,
	List<ReporteAvanceDTO> reportes,
	Recompensas recompensas,
	Configuracion configuracion,
	String publicSlug,
	Integer slaHorasValidacion,
	String tipoConsecuencia,
	Boolean esPlantilla,
	String visibility,
	Instant fechaCreacion,
	Instant fechaActualizacion,
	Instant updatedAt
) {
	public static record Recompensas(
		Integer puntos,
		List<String> badges,
		String consecuencias
	) {}
	public static record Configuracion(
		Boolean requiereEvidencia,
		String tipoEvidencia,
		String frecuenciaReporte,
		Integer validacionMinima
	) {}
	public static record ReporteAvanceDTO(
		Long id,
		Instant fecha,
		String descripcion,
		EvidenciaDTO evidencia,
		List<ValidacionReporteDTO> validaciones,
		String estado
	) {}
	public static record EvidenciaDTO(
		Long id,
		String tipo,
		String contenido,
		String descripcion
	) {}
	public static record ValidacionReporteDTO(
		Long id,
		Long validadorId,
		String estado,
		String comentario,
		Instant fecha
	) {}
}
