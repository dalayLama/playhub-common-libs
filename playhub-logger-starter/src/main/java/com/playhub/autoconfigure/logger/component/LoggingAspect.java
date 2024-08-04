package com.playhub.autoconfigure.logger.component;

import com.playhub.autoconfigure.logger.Logging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {

    private final LogFormatterManager logFormatterManager;

    @Value("${playhub.logging.level:INFO}")
    private Level logLevel;

    @Around("@annotation(logging)")
    public Object aroundLogging(
        ProceedingJoinPoint joinPoint,
        Logging logging
    ) throws Throwable {
        Class<? extends LogFormatter> formatter = logging.formatter();
        LogFormatter logFormatter = logFormatterManager.getLogFormatter(formatter);
        Map<String, Object> params = getMethodParams(joinPoint);

        if (logging.input()) {
            String inputMessage = logging.inputTemplate().formatted(
                ClassUtils.getUserClass(joinPoint.getThis().getClass()).getSimpleName(),
                joinPoint.getSignature().getName()
            );
            log(logFormatter::formatInput, inputMessage, params);
        }

        Object result = joinPoint.proceed();

        if (logging.output()) {
            String outputMessage = logging.outputTemplate().formatted(
                ClassUtils.getUserClass(joinPoint.getThis().getClass()).getSimpleName(),
                joinPoint.getSignature().getName()
            );
            log(logFormatter::formatOutput, outputMessage, result);
        }

        return result;
    }

    private <T> void log(
        Function<T, String> function,
        String message,
        T arg
    ) {
        try {
            log.atLevel(logLevel).log("{} {}", message, function.apply(arg));
        } catch (Exception e) {
            log.error("Failed to execute formatter on arg %s".formatted(arg), e);
        }
    }

    private Map<String, Object> getMethodParams(ProceedingJoinPoint joinPoint) {
        Map<String, Object> param = new HashMap<>();
        Object[] paramValues = joinPoint.getArgs();
        if (Objects.nonNull(paramValues) && paramValues.length > 0) {
            String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
            for (int i = 0; i < paramNames.length; i++) {
                param.put(paramNames[i], paramValues[i]);
            }
        }
        return param;
    }
}
