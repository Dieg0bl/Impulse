package com.impulse.shared.error;

/**
 * Exception thrown when validation fails
 * Following IMPULSE v1.0 §2 homogeneous error format
 */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message, ErrorCodes.VALIDATION_ERROR);
    }

    public ValidationException(String field, String reason) {
        super(String.format("Validation failed for field '%s': %s", field, reason), ErrorCodes.VALIDATION_ERROR);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, ErrorCodes.VALIDATION_ERROR, cause);
    }
}
