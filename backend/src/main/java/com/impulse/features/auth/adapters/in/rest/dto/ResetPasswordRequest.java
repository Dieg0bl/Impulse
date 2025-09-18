package com.impulse.features.auth.adapters.in.rest.dto;

/**
 * ResetPasswordRequest - DTO de request para reset password endpoint
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class ResetPasswordRequest {

    private String token;
    private String newPassword;
    private String userAgent;
    private String ipAddress;

    // Constructors
    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String token, String newPassword, String userAgent, String ipAddress) {
        this.token = token;
        this.newPassword = newPassword;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    @Override
    public String toString() {
        return "ResetPasswordRequest{" +
                "token='[HIDDEN]'" +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
