package com.impulse.domain.evidencia;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Validador para Evidencia. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class EvidenciaValidator {

    // Constructor privado para evitar instanciación
    private EvidenciaValidator() {
        throw new UnsupportedOperationException("No se puede instanciar EvidenciaValidator");
    }

    /**
     * Valida un EvidenciaDTO antes de operaciones críticas (registro, actualización).
     * Cumple compliance: RGPD, ISO 27001, ENS.
     * @param dto EvidenciaDTO a validar
     * @throws IllegalArgumentException si alguna validación falla
     */
    public static void validar(EvidenciaDTO dto) {
        if (dto == null) throw new IllegalArgumentException("EvidenciaDTO no puede ser null");
        if (dto.getTipo() == null || dto.getTipo().isBlank())
            throw new IllegalArgumentException("El tipo de evidencia es obligatorio");
        if (dto.getUrl() == null || dto.getUrl().isBlank())
            throw new IllegalArgumentException("La URL de la evidencia es obligatoria");
        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria");
        if (dto.getRetoId() == null)
            throw new IllegalArgumentException("El retoId es obligatorio");
        if (dto.getValidado() == null || dto.getValidado().isBlank())
            throw new IllegalArgumentException("El estado de validación es obligatorio");
        if (dto.getCreatedAt() == null)
            throw new IllegalArgumentException("createdAt es obligatorio");
        if (dto.getUpdatedAt() == null)
            throw new IllegalArgumentException("updatedAt es obligatorio");
        if (dto.getCreatedBy() == null || dto.getCreatedBy().isBlank())
            throw new IllegalArgumentException("createdBy es obligatorio");
        if (dto.getUpdatedBy() == null || dto.getUpdatedBy().isBlank())
            throw new IllegalArgumentException("updatedBy es obligatorio");
    }
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static Set<ConstraintViolation<Evidencia>> validate(Evidencia evidencia) {
        return validator.validate(evidencia);
    }
}
