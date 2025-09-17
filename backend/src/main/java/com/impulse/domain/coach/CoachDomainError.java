package com.impulse.domain.coach;

/**
 * CoachDomainError - Excepción específica del dominio de coach
 */
public class CoachDomainError extends RuntimeException {
    public CoachDomainError(String message) {
        super(message);
    }
    public CoachDomainError(String message, Throwable cause) {
        super(message, cause);
    }
}
