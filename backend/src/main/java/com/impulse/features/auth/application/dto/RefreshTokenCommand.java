package com.impulse.features.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * RefreshTokenCommand - DTO para comando de refresh token
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class RefreshTokenCommand {

    @NotBlank(message = "Refresh token is required")
    private final String refreshToken;

    private final String userAgent;
    private final String ipAddress;

    public RefreshTokenCommand(String refreshToken, String userAgent, String ipAddress) {
        this.refreshToken = refreshToken;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters
    public String getRefreshToken() { return refreshToken; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }

    @Override
    public String toString() {
        return "RefreshTokenCommand{" +
                "refreshToken='[HIDDEN]'" +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
