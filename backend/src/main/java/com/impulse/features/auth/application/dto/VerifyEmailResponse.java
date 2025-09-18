package com.impulse.features.auth.application.dto;

/**
 * VerifyEmailResponse - DTO para respuesta de verificación de email
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class VerifyEmailResponse {

    private final boolean success;
    private final String message;

    private VerifyEmailResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static VerifyEmailResponse success() {
        return new VerifyEmailResponse(true, "Email successfully verified");
    }

    public static VerifyEmailResponse failure(String message) {
        return new VerifyEmailResponse(false, message);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "VerifyEmailResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
