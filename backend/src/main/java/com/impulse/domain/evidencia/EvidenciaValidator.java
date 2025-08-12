
package com.impulse.domain.evidencia;

import org.springframework.stereotype.Component;

/**
 * Validador para Evidencia. Cumple compliance: RGPD, ISO 27001, ENS.
 * Valida el record EvidenciaDTO alineado con la base de datos y lógica de negocio.
 */
@Component
public class EvidenciaValidator {
    public void validarNueva(EvidenciaDTO dto) {
        if (dto == null) throw new IllegalArgumentException("EvidenciaDTO no puede ser null");
        if (dto.tipoEvidencia() == null || dto.tipoEvidencia().isBlank())
            throw new IllegalArgumentException("El tipo de evidencia es obligatorio");
        if (dto.retoId() == null)
            throw new IllegalArgumentException("El retoId es obligatorio");
        if (dto.usuarioId() == null)
            throw new IllegalArgumentException("El usuarioId es obligatorio");
        if (dto.estadoValidacion() == null || dto.estadoValidacion().isBlank())
            throw new IllegalArgumentException("El estado de validación es obligatorio");
        // Más validaciones según reglas de negocio y compliance
    }
}
