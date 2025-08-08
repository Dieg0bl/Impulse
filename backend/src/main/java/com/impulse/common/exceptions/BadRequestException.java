package com.impulse.common.exceptions;


/**
 * Excepción para peticiones inválidas (HTTP 400).
 * Cumple compliance: auditabilidad, trazabilidad, REST.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
