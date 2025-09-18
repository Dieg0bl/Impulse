package com.impulse.features.auth.application.dto;

/**
 * ResetPasswordResponse - DTO para respuesta de reset de contraseña
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class ResetPasswordResponse {

    private final boolean success;
    private final String message;

    private ResetPasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ResetPasswordResponse success() {
        return new ResetPasswordResponse(true, "Password successfully reset");
    }

    public static ResetPasswordResponse failure(String message) {
        return new ResetPasswordResponse(false, message);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "ResetPasswordResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
