package com.impulse.domain.security.pii;

import java.lang.annotation.*;

/**
 * Dominio: Anotación PII para marcar campos con información personal identificable.
 * Implementación real debe residir en infrastructure.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface PII {
    enum Level { LOW, MEDIUM, HIGH }
    Level value() default Level.LOW;
}
