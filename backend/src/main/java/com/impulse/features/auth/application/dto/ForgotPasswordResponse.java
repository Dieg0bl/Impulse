package com.impulse.features.auth.application.dto;

/**
 * ForgotPasswordResponse - Respuesta del caso de uso de forgot password
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class ForgotPasswordResponse {

    private final boolean success;
    private final String message;

    public ForgotPasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static ForgotPasswordResponse success() {
        return new ForgotPasswordResponse(true, "Password reset email sent if account exists");
    }

    public static ForgotPasswordResponse success(String message) {
        return new ForgotPasswordResponse(true, message);
    }
}
