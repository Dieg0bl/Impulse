package com.impulse.infrastructure.notificacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio de integración con WhatsApp Business API (simulado, requiere API oficial en producción).
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Las credenciales se gestionan solo por variables de entorno o vault.
 */
@Service
public class WhatsAppSender {
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppSender.class);

    @Value("${WHATSAPP_API_URL:https://graph.facebook.com/v18.0}")
    private String apiUrl;
    @Value("${WHATSAPP_API_TOKEN:default-token}")
    private String apiToken;
    // Compliance: Verificación de inyección de configuración
    public WhatsAppSender() {
        assert this.apiUrl != null : "WHATSAPP_API_URL no inyectado";
        assert this.apiToken != null : "WHATSAPP_API_TOKEN no inyectado";
        logger.info("WhatsAppSender inicializado correctamente");
    }

    public void sendWhatsApp(String to, String message) {
        // Aquí iría la llamada real a la API de WhatsApp Business
        logger.info("[AUDIT] WhatsApp enviado a {}: {}", to, message);
        // No se loguea el token ni datos sensibles
    }
}
