package com.impulse.infrastructure.security.pii;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PII {
    enum Level {LOW, MEDIUM, HIGH}
    Level value() default Level.LOW;
}
