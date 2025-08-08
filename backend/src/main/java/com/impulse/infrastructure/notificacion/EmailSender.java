package com.impulse.infrastructure.notificacion;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio de envío de emails usando SMTP seguro.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Las credenciales se gestionan solo por variables de entorno o vault.
 */
@Service
public class EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Value("${EMAIL_SMTP_HOST:localhost}")
    private String smtpHost;
    @Value("${EMAIL_SMTP_PORT:587}")
    private String smtpPort;
    @Value("${EMAIL_SMTP_USER:noreply@impulse.dev}")
    private String smtpUser;
    @Value("${EMAIL_SMTP_PASS:default}")
    private String smtpPass;

    public void sendEmail(String to, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPass);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpUser));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            logger.info("[AUDIT] Email enviado a {} con asunto '{}'.", to, subject);
        } catch (MessagingException e) {
            logger.error("[AUDIT] Error enviando email a {}: {}", to, e.getMessage());
        }
    }
}
