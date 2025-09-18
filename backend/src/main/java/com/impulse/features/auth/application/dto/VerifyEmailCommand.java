package com.impulse.features.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * VerifyEmailCommand - DTO para comando de verificación de email
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class VerifyEmailCommand {

    @NotBlank(message = "Token is required")
    private final String token;

    private final String userAgent;
    private final String ipAddress;

    public VerifyEmailCommand(String token, String userAgent, String ipAddress) {
        this.token = token;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters
    public String getToken() { return token; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }

    @Override
    public String toString() {
        return "VerifyEmailCommand{" +
                "token='[HIDDEN]'" +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
