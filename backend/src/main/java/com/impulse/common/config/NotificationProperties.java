package com.impulse.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración de notificaciones externas para mapear propiedades del application.yml
 * Elimina warnings de propiedades no reconocidas como EMAIL_SMTP_*, WHATSAPP_*, TELEGRAM_*
 */
@Component
@ConfigurationProperties(prefix = "notifications")
public class NotificationProperties {
    
    // Configuración Email SMTP
    private String emailSmtpHost;
    private String emailSmtpPort;
    private String emailSmtpUser;
    private String emailSmtpPass;
    
    // Configuración WhatsApp
    private String whatsappApiUrl;
    private String whatsappApiToken;
    
    // Configuración Telegram
    private String telegramBotToken;
    private String telegramChatId;
    
    // Getters y Setters
    public String getEmailSmtpHost() {
        return emailSmtpHost;
    }
    
    public void setEmailSmtpHost(String emailSmtpHost) {
        this.emailSmtpHost = emailSmtpHost;
    }
    
    public String getEmailSmtpPort() {
        return emailSmtpPort;
    }
    
    public void setEmailSmtpPort(String emailSmtpPort) {
        this.emailSmtpPort = emailSmtpPort;
    }
    
    public String getEmailSmtpUser() {
        return emailSmtpUser;
    }
    
    public void setEmailSmtpUser(String emailSmtpUser) {
        this.emailSmtpUser = emailSmtpUser;
    }
    
    public String getEmailSmtpPass() {
        return emailSmtpPass;
    }
    
    public void setEmailSmtpPass(String emailSmtpPass) {
        this.emailSmtpPass = emailSmtpPass;
    }
    
    public String getWhatsappApiUrl() {
        return whatsappApiUrl;
    }
    
    public void setWhatsappApiUrl(String whatsappApiUrl) {
        this.whatsappApiUrl = whatsappApiUrl;
    }
    
    public String getWhatsappApiToken() {
        return whatsappApiToken;
    }
    
    public void setWhatsappApiToken(String whatsappApiToken) {
        this.whatsappApiToken = whatsappApiToken;
    }
    
    public String getTelegramBotToken() {
        return telegramBotToken;
    }
    
    public void setTelegramBotToken(String telegramBotToken) {
        this.telegramBotToken = telegramBotToken;
    }
    
    public String getTelegramChatId() {
        return telegramChatId;
    }
    
    public void setTelegramChatId(String telegramChatId) {
        this.telegramChatId = telegramChatId;
    }
}
