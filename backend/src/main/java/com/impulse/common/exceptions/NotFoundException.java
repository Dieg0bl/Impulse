package com.impulse.common.exceptions;


/**
 * Excepción para recursos no encontrados (HTTP 404).
 * Cumple compliance: auditabilidad, trazabilidad, REST.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
