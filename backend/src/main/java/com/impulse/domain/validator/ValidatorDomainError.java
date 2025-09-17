package com.impulse.domain.validator;

public class ValidatorDomainError extends RuntimeException {
    public ValidatorDomainError(String message) { super(message); }
}
