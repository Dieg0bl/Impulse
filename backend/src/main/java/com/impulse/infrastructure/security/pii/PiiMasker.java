package com.impulse.infrastructure.security.pii;

public final class PiiMasker {
    private PiiMasker() {}
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        var parts = email.split("@", 2);
        var local = parts[0];
        var dom = parts[1];
        var maskedLocal = local.length() <= 1 ? "*" : local.charAt(0) + "*".repeat(Math.max(1, local.length() - 1));
        return maskedLocal + "@" + dom;
    }
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "***";
        return "***" + phone.substring(phone.length() - 4);
    }
}
