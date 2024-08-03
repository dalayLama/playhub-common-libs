package com.playhub.autoconfigure.logger;

import com.playhub.autoconfigure.logger.component.DefaultLogFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
    boolean input() default false;
    boolean output() default false;
    Class<?> formatter() default DefaultLogFormatter.class;
}
