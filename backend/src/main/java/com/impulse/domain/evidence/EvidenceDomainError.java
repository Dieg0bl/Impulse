package com.impulse.domain.evidence;

/**
 * EvidenceDomainError - Excepción específica del dominio de evidencias
 */
public class EvidenceDomainError extends RuntimeException {

    public EvidenceDomainError(String message) {
        super(message);
    }

    public EvidenceDomainError(String message, Throwable cause) {
        super(message, cause);
    }
}
