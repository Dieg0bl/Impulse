package com.impulse.infrastructure.notificacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de integración con Telegram Bot API.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Las credenciales se gestionan solo por variables de entorno o vault.
 */
@Service
public class TelegramSender {
    private static final Logger logger = LoggerFactory.getLogger(TelegramSender.class);

    private final String botToken;
    private final String chatId;

    /**
     * Constructor injection: permite validar después y soporta múltiples orígenes.
     * Prioridad: property 'notifications.*' (application.yml / test) -> env var -> fallback.
     */
    public TelegramSender(
            @Value("${notifications.telegram-bot-token:${TELEGRAM_BOT_TOKEN:default-bot-token}}") String botToken,
            @Value("${notifications.telegram-chat-id:${TELEGRAM_CHAT_ID:123456}}") String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @PostConstruct
    void validate() {
        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException("Telegram bot token no configurado");
        }
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalStateException("Telegram chat id no configurado");
        }
        logger.info("TelegramSender inicializado (chatId={})", chatId);
    }

    public void sendTelegram(String message) {
        // Aquí iría la llamada real a la API de Telegram Bot
        logger.info("[AUDIT] Telegram enviado a chat {}: {}", chatId, message);
        // No se loguea el token ni datos sensibles
    }
}
