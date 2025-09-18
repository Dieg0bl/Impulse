package com.impulse.shared.utils;

import java.time.Clock;

/** Provides clock abstraction to allow deterministic testing */
public final class ClockProvider {
    private static Clock clock = Clock.systemUTC();
    private ClockProvider() {}
    public static Clock getClock() { return clock; }
    public static void setClock(Clock newClock) { clock = newClock; }
}
