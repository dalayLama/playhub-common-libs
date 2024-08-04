package com.playhub.autoconfigure.logger.component;

import java.lang.reflect.InvocationTargetException;

public interface LogFormatterManager {

    LogFormatter getLogFormatter(Class<? extends LogFormatter> formatter) throws NoSuchMethodException,
        InvocationTargetException, InstantiationException, IllegalAccessException;
}
