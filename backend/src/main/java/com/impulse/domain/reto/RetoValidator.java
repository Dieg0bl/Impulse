package com.impulse.domain.reto;

import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Validador para Reto. Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Component
public class RetoValidator {

    private final Validator validator;

    // Constructor para Spring
    public RetoValidator() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * Valida un RetoDTO antes de operaciones críticas (registro, actualización).
     * Cumple compliance: RGPD, ISO 27001, ENS.
     * @param dto RetoDTO a validar
     * @throws IllegalArgumentException si alguna validación falla
     */
    public void validar(RetoDTO dto) {
        validarNoNull(dto);
        validarTitulo(dto);
        validarDescripcion(dto);
        validarFechas(dto);
        validarEstado(dto);
        validarUsuarioId(dto);
        validarCreatedAt(dto);
        validarUpdatedAt(dto);
        validarCreatedBy(dto);
        validarUpdatedBy(dto);
    }

    private void validarNoNull(RetoDTO dto) {
        if (dto == null) throw new IllegalArgumentException("RetoDTO no puede ser null");
    }

    private void validarTitulo(RetoDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank())
            throw new IllegalArgumentException("El título es obligatorio");
    }

    private void validarDescripcion(RetoDTO dto) {
        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria");
    }

    private void validarFechas(RetoDTO dto) {
        if (dto.getFechaInicio() == null)
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        if (dto.getFechaFin() != null && dto.getFechaFin().isBefore(dto.getFechaInicio()))
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
    }

    private void validarEstado(RetoDTO dto) {
        if (dto.getEstado() == null || dto.getEstado().isBlank())
            throw new IllegalArgumentException("El estado es obligatorio");
    }

    private void validarUsuarioId(RetoDTO dto) {
        if (dto.getUsuarioId() == null)
            throw new IllegalArgumentException("El usuarioId es obligatorio");
    }

    private void validarCreatedAt(RetoDTO dto) {
        if (dto.getCreatedAt() == null)
            throw new IllegalArgumentException("createdAt es obligatorio");
    }

    private void validarUpdatedAt(RetoDTO dto) {
        if (dto.getUpdatedAt() == null)
            throw new IllegalArgumentException("updatedAt es obligatorio");
    }

    private void validarCreatedBy(RetoDTO dto) {
        if (dto.getCreatedBy() == null || dto.getCreatedBy().isBlank())
            throw new IllegalArgumentException("createdBy es obligatorio");
    }

    private void validarUpdatedBy(RetoDTO dto) {
        if (dto.getUpdatedBy() == null || dto.getUpdatedBy().isBlank())
            throw new IllegalArgumentException("updatedBy es obligatorio");
    }

    public Set<ConstraintViolation<Reto>> validate(Reto reto) {
        return validator.validate(reto);
    }
}
