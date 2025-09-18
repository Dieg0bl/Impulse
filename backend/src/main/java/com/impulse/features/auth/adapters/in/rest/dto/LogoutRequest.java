package com.impulse.features.auth.adapters.in.rest.dto;

/**
 * LogoutRequest - DTO de request para logout endpoint
 * Anexo 1 IMPULSE v1.0 - Autenticación
 */
public class LogoutRequest {

    private String refreshToken;
    private String userAgent;
    private String ipAddress;
    private boolean logoutFromAllDevices;

    // Constructors
    public LogoutRequest() {}

    public LogoutRequest(String refreshToken, String userAgent, String ipAddress, boolean logoutFromAllDevices) {
        this.refreshToken = refreshToken;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.logoutFromAllDevices = logoutFromAllDevices;
    }

    // Getters and Setters
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public boolean isLogoutFromAllDevices() { return logoutFromAllDevices; }
    public void setLogoutFromAllDevices(boolean logoutFromAllDevices) { this.logoutFromAllDevices = logoutFromAllDevices; }

    @Override
    public String toString() {
        return "LogoutRequest{" +
                "refreshToken='[HIDDEN]'" +
                ", userAgent='" + userAgent + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", logoutFromAllDevices=" + logoutFromAllDevices +
                '}';
    }
}
