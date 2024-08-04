package com.playhub.autoconfigure.logger.component;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LogFormatterManagerImpl implements LogFormatterManager {

    private final Map<Class<?>, LogFormatter> logFormatters = new ConcurrentHashMap<>();

    @Override
    public LogFormatter getLogFormatter(Class<? extends LogFormatter> formatter) {
        return logFormatters.computeIfAbsent(formatter, key -> createLogFormatter(formatter));
    }

    private LogFormatter createLogFormatter(Class<? extends LogFormatter> formatter) {
        log.debug("Creating log formatter for class [{}]", formatter.getSimpleName());
        try {
            Constructor<? extends LogFormatter> constructor = formatter.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException
                 | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
