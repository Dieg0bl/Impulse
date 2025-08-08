package com.impulse.common.util;

import java.util.regex.Pattern;

/**
 * Utilidad para validaciones comunes (email, teléfono, etc.).
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class ValidationUtil {
    // Constructor privado para evitar instanciación
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{9,15}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}
