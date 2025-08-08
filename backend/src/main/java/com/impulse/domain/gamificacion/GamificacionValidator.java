package com.impulse.domain.gamificacion;

import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Validador para Gamificacion. Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Component
public class GamificacionValidator {

    /**
     * Valida un GamificacionDTO antes de operaciones críticas (registro, actualización).
     * Cumple compliance: RGPD, ISO 27001, ENS.
     * @param dto GamificacionDTO a validar
     * @throws IllegalArgumentException si alguna validación falla
     */
    public void validar(GamificacionDTO dto) {
        if (dto == null) throw new IllegalArgumentException("GamificacionDTO no puede ser null");
        if (dto.getTipo() == null || dto.getTipo().isBlank())
            throw new IllegalArgumentException("El tipo de gamificación es obligatorio");
        if (dto.getPuntos() == null || dto.getPuntos() < 0)
            throw new IllegalArgumentException("Los puntos deben ser >= 0");
        if (dto.getUsuarioId() == null)
            throw new IllegalArgumentException("El usuarioId es obligatorio");
        if (dto.getCreatedAt() == null)
            throw new IllegalArgumentException("createdAt es obligatorio");
        if (dto.getUpdatedAt() == null)
            throw new IllegalArgumentException("updatedAt es obligatorio");
        if (dto.getCreatedBy() == null || dto.getCreatedBy().isBlank())
            throw new IllegalArgumentException("createdBy es obligatorio");
        if (dto.getUpdatedBy() == null || dto.getUpdatedBy().isBlank())
            throw new IllegalArgumentException("updatedBy es obligatorio");
    }

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public Set<ConstraintViolation<Gamificacion>> validate(Gamificacion gamificacion) {
        return validator.validate(gamificacion);
    }
}
