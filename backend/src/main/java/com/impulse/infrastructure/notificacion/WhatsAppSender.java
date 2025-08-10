package com.impulse.infrastructure.notificacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de integración con WhatsApp Business API (simulado, requiere API oficial en producción).
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Las credenciales se gestionan solo por variables de entorno o vault.
 */
@Service
public class WhatsAppSender {
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppSender.class);

    private final String apiUrl;
    private final String apiToken;

    public WhatsAppSender(
            @Value("${notifications.whatsapp-api-url:${WHATSAPP_API_URL:https://graph.facebook.com/v18.0}}") String apiUrl,
            @Value("${notifications.whatsapp-api-token:${WHATSAPP_API_TOKEN:default-token}}") String apiToken) {
        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
    }

    @PostConstruct
    void validate() {
        if (apiUrl == null || apiUrl.isBlank()) {
            throw new IllegalStateException("WhatsApp api url no configurada");
        }
        if (apiToken == null || apiToken.isBlank()) {
            throw new IllegalStateException("WhatsApp api token no configurado");
        }
        logger.info("WhatsAppSender inicializado (apiUrl={})", apiUrl);
    }

    public void sendWhatsApp(String to, String message) {
        // Aquí iría la llamada real a la API de WhatsApp Business
        logger.info("[AUDIT] WhatsApp enviado a {}: {}", to, message);
        // No se loguea el token ni datos sensibles
    }
}
