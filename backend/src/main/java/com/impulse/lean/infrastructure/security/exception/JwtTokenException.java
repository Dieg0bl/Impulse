package com.impulse.lean.infrastructure.security.exception;

/**
 * Exception thrown when JWT token operations fail
 */
public class JwtTokenException extends Exception {
    
    public JwtTokenException(String message) {
        super(message);
    }
    
    public JwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
