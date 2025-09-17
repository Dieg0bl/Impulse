package com.impulse.domain.user;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email - Value Object para direcciones de correo electrónico
 */
public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private final String value;

    private Email(String value) {
        this.value = Objects.requireNonNull(value, "Email value cannot be null");
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }

    public static Email of(String value) {
        return new Email(value.toLowerCase().trim());
    }

    private static boolean isValid(String email) {
        return email != null &&
               !email.trim().isEmpty() &&
               EMAIL_PATTERN.matcher(email).matches();
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Email{" + value + '}';
    }
}
