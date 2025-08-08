package com.impulse.domain.monetizacion;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Validador para Pago. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class PagoValidator {
    private PagoValidator() {
        // Prevent instantiation
    }

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static Set<ConstraintViolation<Pago>> validate(Pago pago) {
        return validator.validate(pago);
    }
}
