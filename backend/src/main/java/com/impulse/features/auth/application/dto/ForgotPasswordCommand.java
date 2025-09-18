package com.impulse.features.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ForgotPasswordCommand - Comando para solicitar reset de contraseña
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class ForgotPasswordCommand {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private final String email;

    private final String userAgent;
    private final String ipAddress;

    public ForgotPasswordCommand(String email, String userAgent, String ipAddress) {
        this.email = email;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    public String getEmail() {
        return email;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
