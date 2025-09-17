package com.impulse.domain.validation;

public class ValidationDomainError extends RuntimeException {
    public ValidationDomainError(String message) { super(message); }
}
