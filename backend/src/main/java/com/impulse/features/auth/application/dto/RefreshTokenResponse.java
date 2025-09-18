package com.impulse.features.auth.application.dto;

/**
 * RefreshTokenResponse - DTO para respuesta de refresh token
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class RefreshTokenResponse {

    private final boolean success;
    private final String message;
    private final String accessToken;
    private final String refreshToken;
    private final Long expiresIn;

    private RefreshTokenResponse(boolean success, String message, String accessToken, String refreshToken, Long expiresIn) {
        this.success = success;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public static RefreshTokenResponse success(String accessToken, String refreshToken, Long expiresIn) {
        return new RefreshTokenResponse(true, "Tokens refreshed successfully", accessToken, refreshToken, expiresIn);
    }

    public static RefreshTokenResponse failure(String message) {
        return new RefreshTokenResponse(false, message, null, null, null);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public Long getExpiresIn() { return expiresIn; }

    @Override
    public String toString() {
        return "RefreshTokenResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", hasTokens=" + (accessToken != null) +
                '}';
    }
}
