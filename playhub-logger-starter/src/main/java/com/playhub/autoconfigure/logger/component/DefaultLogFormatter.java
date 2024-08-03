package com.playhub.autoconfigure.logger.component;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultLogFormatter implements LogFormatter {

    @Override
    public String formatInput(Object... objects) {
        return Arrays.stream(objects)
            .filter(Objects::nonNull)
            .map(obj -> String.format("%s=%s", obj.getClass().getName(), obj))
            .collect(Collectors.joining(", "));
    }

    @Override
    public String formatOutput(Object object) {
        return Objects.nonNull(object)
            ? object.toString()
            : "";
    }
}
