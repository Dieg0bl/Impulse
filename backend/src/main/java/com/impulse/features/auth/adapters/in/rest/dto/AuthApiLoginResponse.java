package com.impulse.features.auth.adapters.in.rest.dto;

/**
 * API Response DTO for user login
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class AuthApiLoginResponse {
    private String code;
    private String message;
    private String correlationId;
    private LoginData data;

    public static class LoginData {
        private Long userId;
        private String username;
        private String accessToken;
        private String refreshToken;
        private boolean emailVerified;
        private long expiresIn;

        public LoginData(Long userId, String username, String accessToken,
                        String refreshToken, boolean emailVerified, long expiresIn) {
            this.userId = userId;
            this.username = username;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.emailVerified = emailVerified;
            this.expiresIn = expiresIn;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public boolean isEmailVerified() { return emailVerified; }
        public long getExpiresIn() { return expiresIn; }
    }

    public AuthApiLoginResponse(String code, String message, String correlationId, LoginData data) {
        this.code = code;
        this.message = message;
        this.correlationId = correlationId;
        this.data = data;
    }

    // Getters
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getCorrelationId() { return correlationId; }
    public LoginData getData() { return data; }
}
