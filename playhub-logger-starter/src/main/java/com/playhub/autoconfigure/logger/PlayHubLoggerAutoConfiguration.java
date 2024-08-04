package com.playhub.autoconfigure.logger;

import com.playhub.autoconfigure.logger.component.LogFormatterManager;
import com.playhub.autoconfigure.logger.component.LogFormatterManagerImpl;
import com.playhub.autoconfigure.logger.component.LoggingAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "playhub.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PlayHubLoggerAutoConfiguration {

    @Bean
    public LogFormatterManager logFormatterManager() {
        return new LogFormatterManagerImpl();
    }

    @Bean
    public LoggingAspect loggingAspect(LogFormatterManager logFormatterManager) {
        return new LoggingAspect(logFormatterManager);
    }

}
