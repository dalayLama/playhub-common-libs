package com.playhub.autoconfigure.logger.component;

import com.playhub.autoconfigure.logger.Logging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {

    private static final Class<?> DEFAULT_CLASS = Object.class;
    private final Map<Class<?>, LogFormatter> logFormatters;

    @Around("@annotation(logging)")
    public Object aroundLogging(
        ProceedingJoinPoint joinPoint,
        Logging logging
    ) throws Throwable {
        Class<?> formatter = logging.formatter();
        LogFormatter logFormatter = logFormatters.get(formatter);
        if (Objects.isNull(logFormatter)) {
            log.warn("Not found log formatter for class [{}]", formatter.getSimpleName());
            logFormatter = logFormatters.get(DEFAULT_CLASS);
        }

        boolean isDefault = Objects.equals(formatter, DEFAULT_CLASS);
        LogFormatter defaultFormatter = isDefault ? logFormatters.get(DEFAULT_CLASS) : logFormatter;
        if (logging.input()) {
            log(isDefault, formatter, logFormatter::formatInput, defaultFormatter::formatInput, joinPoint.getArgs());
        }

        Object result = joinPoint.proceed();

        if (logging.output()) {
            log(isDefault, formatter, logFormatter::formatOutput, defaultFormatter::formatOutput, result);
        }

        return result;
    }

    private <T> void log(
        boolean isDefault,
        Class<?> formatter,
        Function<T, String> function,
        Function<T, String> defaultFunction,
        T arg
    ) {
        try {
            log.debug(function.apply(arg));
        } catch (Exception e) {
            log.warn("Failed to format [{}] by formatter for class [{}]", arg, formatter.getSimpleName());
            if (!isDefault) {
                log.debug(defaultFunction.apply(arg));
            }
        }
    }
}
