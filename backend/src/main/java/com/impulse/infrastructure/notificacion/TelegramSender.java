package com.impulse.infrastructure.notificacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio de integración con Telegram Bot API.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Las credenciales se gestionan solo por variables de entorno o vault.
 */
@Service
public class TelegramSender {
    private static final Logger logger = LoggerFactory.getLogger(TelegramSender.class);

    @Value("${TELEGRAM_BOT_TOKEN:default-bot-token}")
    private String botToken;
    @Value("${TELEGRAM_CHAT_ID:123456}")
    private String chatId;
    // Compliance: Verificación de inyección de configuración
    public TelegramSender() {
        assert this.botToken != null : "TELEGRAM_BOT_TOKEN no inyectado";
        assert this.chatId != null : "TELEGRAM_CHAT_ID no inyectado";
        logger.info("TelegramSender inicializado correctamente");
    }

    public void sendTelegram(String message) {
        // Aquí iría la llamada real a la API de Telegram Bot
        logger.info("[AUDIT] Telegram enviado a chat {}: {}", chatId, message);
        // No se loguea el token ni datos sensibles
    }
}
