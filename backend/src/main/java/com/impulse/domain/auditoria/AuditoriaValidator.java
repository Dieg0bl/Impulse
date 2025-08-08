package com.impulse.domain.auditoria;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Validador para Auditoria. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class AuditoriaValidator {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Constructor privado para prevenir instanciación de esta clase utilitaria.
     */
    private AuditoriaValidator() {
        throw new UnsupportedOperationException("Esta es una clase utilitaria y no debe ser instanciada");
    }

    public static Set<ConstraintViolation<Auditoria>> validate(Auditoria auditoria) {
        return validator.validate(auditoria);
    }
}
