package com.impulse.features.auth.application.dto;

/**
 * LogoutUserResponse - DTO para respuesta de logout
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class LogoutUserResponse {

    private final boolean success;
    private final String message;
    private final int tokensInvalidated;

    private LogoutUserResponse(boolean success, String message, int tokensInvalidated) {
        this.success = success;
        this.message = message;
        this.tokensInvalidated = tokensInvalidated;
    }

    public static LogoutUserResponse success() {
        return new LogoutUserResponse(true, "Successfully logged out", 1);
    }

    public static LogoutUserResponse successAllDevices(int tokensInvalidated) {
        return new LogoutUserResponse(true, "Successfully logged out from all devices", tokensInvalidated);
    }

    public static LogoutUserResponse failure(String message) {
        return new LogoutUserResponse(false, message, 0);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getTokensInvalidated() { return tokensInvalidated; }

    @Override
    public String toString() {
        return "LogoutUserResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", tokensInvalidated=" + tokensInvalidated +
                '}';
    }
}
