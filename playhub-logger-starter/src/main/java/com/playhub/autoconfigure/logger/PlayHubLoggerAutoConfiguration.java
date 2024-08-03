package com.playhub.autoconfigure.logger;

import com.playhub.autoconfigure.logger.component.DefaultLogFormatter;
import com.playhub.autoconfigure.logger.component.LogFormatter;
import com.playhub.autoconfigure.logger.component.LoggingAspect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "playhub.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PlayHubLoggerAutoConfiguration {

    @Bean
    public LogFormatter defaultLogFormatter() {
        return new DefaultLogFormatter();
    }

    @Bean
    public Map<Class<?>, LogFormatter> logFormatters(List<LogFormatter> logFormatters) {
        return logFormatters.stream()
            .collect(Collectors.toMap(Object::getClass, Function.identity()));
    }

    @Bean
    public LoggingAspect loggingAspect(Map<Class<?>, LogFormatter> logFormatters) {
        return new LoggingAspect(logFormatters);
    }

}
