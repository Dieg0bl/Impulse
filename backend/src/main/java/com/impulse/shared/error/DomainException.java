package com.impulse.shared.error;

/**
 * Base domain exception (mapped to {code,message,correlationId})
 * Following IMPULSE v1.0 §2 homogeneous error format
 */
public class DomainException extends RuntimeException {

    private final String code;

    public DomainException(String message) {
        super(message);
        this.code = ErrorCodes.DOMAIN_ERROR;
    }

    public DomainException(String message, String code) {
        super(message);
        this.code = code;
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorCodes.DOMAIN_ERROR;
    }

    public DomainException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
