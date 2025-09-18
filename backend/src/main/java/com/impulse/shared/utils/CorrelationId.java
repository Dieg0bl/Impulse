package com.impulse.shared.utils;

import java.util.UUID;

/** Correlation Id utility (request scoped in filters) */
public final class CorrelationId {
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();
    private CorrelationId() {}
    public static String getOrGenerate() { return CURRENT.get() != null ? CURRENT.get() : generate(); }
    public static String generate() { String id = UUID.randomUUID().toString(); CURRENT.set(id); return id; }
    public static void set(String id) { CURRENT.set(id); }
    public static void clear() { CURRENT.remove(); }
}
