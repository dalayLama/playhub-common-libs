package com.playhub.autoconfigure.logger;

import com.playhub.autoconfigure.logger.component.DefaultLogFormatter;
import com.playhub.autoconfigure.logger.component.LogFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
    boolean input() default false;
    String inputTemplate() default "%s#%s is called:";
    boolean output() default false;
    String outputTemplate() default "%s#%s returns:";
    Class<? extends LogFormatter> formatter() default DefaultLogFormatter.class;
}
