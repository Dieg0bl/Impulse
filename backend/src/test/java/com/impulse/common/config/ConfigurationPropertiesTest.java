package com.impulse.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test para verificar que las propiedades de configuración se cargan correctamente.
 * Verifica que JwtProperties y NotificationProperties mapean correctamente desde YAML.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=este-es-un-secret-jwt-lo-suficientemente-largo-para-cumplir-con-32-caracteres-minimo",
    "jwt.expiration=7200000",
    "notifications.email-smtp-host=test.smtp.com",
    "notifications.email-smtp-port=587",
    "notifications.telegram-bot-token=test-bot-token",
    "impulse.notifications.email-smtp-host=impulse.test.smtp.com",
    "impulse.notifications.email-smtp-port=587",
    "impulse.notifications.telegram-bot-token=impulse-test-bot-token"
})
class ConfigurationPropertiesTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private NotificationProperties notificationProperties;

    @Autowired
    private ImpulseProperties impulseProperties;

    @Test
    void testJwtPropertiesLoaded() {
        assertNotNull(jwtProperties, "JwtProperties debe ser inyectado");
        assertEquals("este-es-un-secret-jwt-lo-suficientemente-largo-para-cumplir-con-32-caracteres-minimo", jwtProperties.getSecret(), "JWT secret debe cargar correctamente");
        assertEquals(7200000L, jwtProperties.getExpiration(), "JWT expiration debe cargar correctamente");
    }

    @Test
    void testNotificationPropertiesLoaded() {
        assertNotNull(notificationProperties, "NotificationProperties debe ser inyectado");
        assertEquals("test.smtp.com", notificationProperties.getEmailSmtpHost(), "Email SMTP host debe cargar correctamente");
        assertEquals("587", notificationProperties.getEmailSmtpPort(), "Email SMTP port debe cargar correctamente");
        assertEquals("test-bot-token", notificationProperties.getTelegramBotToken(), "Telegram bot token debe cargar correctamente");
    }

    @Test
    void testImpulsePropertiesLoaded() {
        assertNotNull(impulseProperties, "ImpulseProperties debe ser inyectado");
        assertNotNull(impulseProperties.getNotifications(), "ImpulseProperties.notifications debe ser inyectado");
        assertEquals("impulse.test.smtp.com", impulseProperties.getNotifications().getEmailSmtpHost(), "Impulse Email SMTP host debe cargar correctamente");
        assertEquals(587, impulseProperties.getNotifications().getEmailSmtpPort(), "Impulse Email SMTP port debe cargar correctamente");
        assertEquals("impulse-test-bot-token", impulseProperties.getNotifications().getTelegramBotToken(), "Impulse Telegram bot token debe cargar correctamente");
    }
}
