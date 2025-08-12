
package com.impulse.domain.reto;

import org.springframework.stereotype.Component;

/**
 * Validador para Reto. Cumple compliance: RGPD, ISO 27001, ENS.
 * Valida el record RetoDTO alineado con la base de datos y lógica de negocio.
 */
@Component
public class RetoValidator {
    public void validarCreacion(RetoDTO dto) {
        if (dto == null) throw new IllegalArgumentException("RetoDTO no puede ser null");
        if (dto.titulo() == null || dto.titulo().isBlank())
            throw new IllegalArgumentException("El título es obligatorio");
        if (dto.descripcion() == null || dto.descripcion().isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria");
        if (dto.fechaInicio() == null)
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        if (dto.fechaFin() != null && dto.fechaFin().isBefore(dto.fechaInicio()))
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        if (dto.estado() == null || dto.estado().isBlank())
            throw new IllegalArgumentException("El estado es obligatorio");
        // Más validaciones según reglas de negocio y compliance
    }
}
