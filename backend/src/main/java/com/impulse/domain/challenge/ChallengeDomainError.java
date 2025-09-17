package com.impulse.domain.challenge;

/**
 * ChallengeDomainError - Excepción específica del dominio de challenges
 */
public class ChallengeDomainError extends RuntimeException {

    public ChallengeDomainError(String message) {
        super(message);
    }

    public ChallengeDomainError(String message, Throwable cause) {
        super(message, cause);
    }
}
