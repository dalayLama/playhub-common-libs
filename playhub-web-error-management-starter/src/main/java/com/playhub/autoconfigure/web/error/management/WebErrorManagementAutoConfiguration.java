package com.playhub.autoconfigure.web.error.management;

import com.playhub.autoconfigure.web.error.management.resolvers.AccessDeniedProblemDetailResolver;
import com.playhub.common.web.error.managment.DefaultProblemDetailResolver;
import com.playhub.common.web.error.managment.ProblemDetailResolver;
import com.playhub.common.web.error.managment.ProblemDetailResolverManager;
import com.playhub.common.web.error.managment.TypeAwareProblemDetailResolver;
import com.playhub.autoconfigure.web.error.management.resolvers.ValidationProblemDetailResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@AutoConfiguration
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebErrorManagementAutoConfiguration {

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

    @Primary
    @ConditionalOnMissingBean(name = "problemDetailResolver")
    @Bean("problemDetailResolver")
    public ProblemDetailResolver problemDetailResolver(
            @Autowired(required = false) MessageSource messageSource,
            Collection<TypeAwareProblemDetailResolver> resolvers) {
        log.info("Creating ProblemDetailResolverManager");
        Map<Class<? extends Throwable>, ? extends ProblemDetailResolver> map = resolvers.stream()
                .flatMap(resolver -> resolver.resolvableExceptions().stream()
                        .map(exceptionType -> Map.entry(exceptionType, resolver)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        DefaultProblemDetailResolver defaultResolver = new DefaultProblemDetailResolver(messageSource);
        return new ProblemDetailResolverManager(messageSource, defaultResolver, map);
    }

    @Bean
    public TypeAwareProblemDetailResolver validationProblemDetailResolver(MessageSource messageSource) {
        log.info("Creating ValidationProblemDetailResolver");
        return new ValidationProblemDetailResolver(messageSource);
    }

    @Bean
    @ConditionalOnClass(AccessDeniedException.class)
    public TypeAwareProblemDetailResolver accessDeniedProblemDetailResolver(MessageSource messageSource) {
        log.info("Creating AccessDeniedProblemDetailResolver");
        return new AccessDeniedProblemDetailResolver(messageSource);
    }


    @RestControllerAdvice
    @RequiredArgsConstructor
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static class PlayhubExceptionHandlerController extends ResponseEntityExceptionHandler {

        private final ProblemDetailResolver problemDetailResolver;

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest webRequest) {
            if (ex instanceof ErrorResponse er) {
                return handleExceptionInternal(ex, null, er.getHeaders(), er.getStatusCode(), webRequest);
            }

            ProblemDetail problemDetail = problemDetailResolver.resolve(ex, webRequest);
            HttpStatus httpStatus = HttpStatus.valueOf(problemDetail.getStatus());
            return super.handleExceptionInternal(
                    ex, problemDetail, HttpHeaders.EMPTY, httpStatus, webRequest
            );

        }

        @Override
        protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                                 Object body,
                                                                 HttpHeaders headers,
                                                                 HttpStatusCode statusCode,
                                                                 WebRequest request) {
            log.error(ex.getMessage(), ex);
            if (body == null) {
                body = problemDetailResolver.resolve(ex, statusCode, request);
            }

            return super.handleExceptionInternal(ex, body, headers, statusCode, request);
        }

        @Override
        protected ProblemDetail createProblemDetail(
                Exception ex,
                HttpStatusCode status,
                String defaultDetail,
                @Nullable String detailMessageCode,
                @Nullable Object[] detailMessageArguments,
                WebRequest request) {
            ProblemDetail problemDetail = super.createProblemDetail(
                    ex, status, defaultDetail, detailMessageCode, detailMessageArguments, request
            );
            problemDetailResolver.enrich(problemDetail, ex, request);
            return problemDetail;
        }

        @Override
        protected ResponseEntity<Object> createResponseEntity(Object body,
                                                              HttpHeaders headers,
                                                              HttpStatusCode statusCode,
                                                              WebRequest request) {
            return super.createResponseEntity(body, headers, statusCode, request);
        }
    }

}
