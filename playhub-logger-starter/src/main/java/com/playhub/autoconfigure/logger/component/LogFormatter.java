package com.playhub.autoconfigure.logger.component;

public interface LogFormatter {

    /**
     * Need to check objects for nulls
     *
     * @param objects
     * @return
     */
    String formatInput(Object... objects);

    String formatOutput(Object object);
}
