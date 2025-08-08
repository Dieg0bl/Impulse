package com.impulse.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para cifrado y validación de contraseñas.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class CryptoUtil {
    private CryptoUtil() {
        // Previene la instanciación
    }
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public static String hashPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
