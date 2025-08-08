package com.impulse.monetizacion;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequiresPlan {
    String value() default "secured"; // feature name
}
