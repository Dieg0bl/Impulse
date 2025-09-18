package com.impulse.features.auth.adapters.in.rest.dto;

/**
 * API Response DTO for user registration
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class AuthApiRegisterResponse {
    private String code;
    private String message;
    private String correlationId;
    private RegisterData data;

    public static class RegisterData {
        private Long userId;
        private String username;
        private String email;
        private boolean emailVerificationRequired;

        public RegisterData(Long userId, String username, String email, boolean emailVerificationRequired) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.emailVerificationRequired = emailVerificationRequired;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public boolean isEmailVerificationRequired() { return emailVerificationRequired; }
    }

    public AuthApiRegisterResponse(String code, String message, String correlationId, RegisterData data) {
        this.code = code;
        this.message = message;
        this.correlationId = correlationId;
        this.data = data;
    }

    // Getters
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getCorrelationId() { return correlationId; }
    public RegisterData getData() { return data; }
}
