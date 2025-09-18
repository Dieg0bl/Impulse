package com.impulse.shared.error;

/**
 * Error codes used throughout the application
 * Central error code registry following IMPULSE v1.0 §2 homogeneous error format
 */
public final class ErrorCodes {

    private ErrorCodes() {
        // Utility class
    }

    // Core domain errors
    public static final String DOMAIN_ERROR = "DOMAIN_ERROR";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String CONFLICT = "CONFLICT";
    public static final String IDEMPOTENCY_REPLAY = "IDEMPOTENCY_REPLAY";

    // Security errors
    public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";

    // Auth errors (Anexo 1 §26)
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String USER_INVALID_CREDENTIALS = "USER_INVALID_CREDENTIALS";

    // Challenge errors
    public static final String CHALLENGE_NOT_FOUND = "CHALLENGE_NOT_FOUND";
    public static final String CHALLENGE_INVALID_STATE = "CHALLENGE_INVALID_STATE";
    public static final String CHALLENGE_PARTICIPATION_LIMIT = "CHALLENGE_PARTICIPATION_LIMIT";

    // Evidence errors
    public static final String EVIDENCE_NOT_FOUND = "EVIDENCE_NOT_FOUND";
    public static final String EVIDENCE_INVALID_STATUS = "EVIDENCE_INVALID_STATUS";
    public static final String EVIDENCE_VALIDATION_FAILED = "EVIDENCE_VALIDATION_FAILED";
}
