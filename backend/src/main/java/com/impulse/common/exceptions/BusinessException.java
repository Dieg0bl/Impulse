package com.impulse.common.exceptions;

/**
 * Excepción para errores de negocio. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
