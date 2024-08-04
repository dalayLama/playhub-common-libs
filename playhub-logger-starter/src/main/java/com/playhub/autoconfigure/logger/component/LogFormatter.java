package com.playhub.autoconfigure.logger.component;

import java.util.Map;

public interface LogFormatter {

    String formatInput(Map<String, Object> params);

    String formatOutput(Object object);
}
