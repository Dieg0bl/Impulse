package com.impulse.features.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * LogoutUserCommand - DTO para comando de logout
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class LogoutUserCommand {

    @NotBlank(message = "Refresh token is required")
    private final String refreshToken;

    private final String userAgent;
    private final String ipAddress;
    private final boolean logoutFromAllDevices;

    public LogoutUserCommand(String refreshToken, String userAgent, String ipAddress, boolean logoutFromAllDevices) {
        this.refreshToken = refreshToken;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.logoutFromAllDevices = logoutFromAllDevices;
    }

    // Getters
    public String getRefreshToken() { return refreshToken; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }
    public boolean isLogoutFromAllDevices() { return logoutFromAllDevices; }

    @Override
    public String toString() {
        return "LogoutUserCommand{" +
                "refreshToken='[HIDDEN]'" +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", logoutFromAllDevices=" + logoutFromAllDevices +
                '}';
    }
}
