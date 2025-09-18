package com.impulse.features.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ResetPasswordCommand - DTO para comando de reset de contraseña
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class ResetPasswordCommand {

    @NotBlank(message = "Token is required")
    private final String token;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private final String newPassword;

    private final String userAgent;
    private final String ipAddress;

    public ResetPasswordCommand(String token, String newPassword, String userAgent, String ipAddress) {
        this.token = token;
        this.newPassword = newPassword;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters
    public String getToken() { return token; }
    public String getNewPassword() { return newPassword; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }

    @Override
    public String toString() {
        return "ResetPasswordCommand{" +
                "token='[HIDDEN]'" +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
