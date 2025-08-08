package com.impulse.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración específicas de IMPULSE.
 * Mapea las propiedades 'impulse.*' desde application.yml
 * Cumple compliance: gestión centralizada de configuraciones de la aplicación
 */
@Component
@ConfigurationProperties(prefix = "impulse")
public class ImpulseProperties {

    private Notifications notifications = new Notifications();

    public Notifications getNotifications() {
        return notifications;
    }

    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    public static class Notifications {
        private String emailSmtpHost;
        private int emailSmtpPort;
        private String emailSmtpUser;
        private String emailSmtpPass;
        private String whatsappApiUrl;
        private String whatsappApiToken;
        private String telegramBotToken;
        private String telegramChatId;

        public String getEmailSmtpHost() {
            return emailSmtpHost;
        }

        public void setEmailSmtpHost(String emailSmtpHost) {
            this.emailSmtpHost = emailSmtpHost;
        }

        public int getEmailSmtpPort() {
            return emailSmtpPort;
        }

        public void setEmailSmtpPort(int emailSmtpPort) {
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
}
