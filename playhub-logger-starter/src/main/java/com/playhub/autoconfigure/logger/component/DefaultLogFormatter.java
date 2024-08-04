package com.playhub.autoconfigure.logger.component;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultLogFormatter implements LogFormatter {

    @Override
    public String formatInput(Map<String, Object> params) {
        return params.entrySet().stream()
            .filter(Objects::nonNull)
            .map(obj -> String.format("%s=%s", obj.getKey(), obj.getValue()))
            .collect(Collectors.joining(", "));
    }

    @Override
    public String formatOutput(Object object) {
        return Objects.nonNull(object)
            ? object.toString()
            : "";
    }
}
