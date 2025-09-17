package com.impulse.domain.user;

/**
 * UserDomainError - Excepción específica del dominio de usuarios
 */
public class UserDomainError extends RuntimeException {

    public UserDomainError(String message) {
        super(message);
    }

    public UserDomainError(String message, Throwable cause) {
        super(message, cause);
    }
}
