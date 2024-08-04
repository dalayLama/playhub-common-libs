package com.playhub.autoconfigure.web.error.management.resolvers;

import com.playhub.common.exceptions.PlayHubErrorCodes;
import com.playhub.common.web.error.managment.AbstractProblemDetailResolver;
import com.playhub.common.web.error.managment.TypeAwareProblemDetailResolver;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationProblemDetailResolver extends AbstractProblemDetailResolver implements TypeAwareProblemDetailResolver {

    public static final String VALIDATION_DETAILS_NAME = "validationDetails";

    public ValidationProblemDetailResolver(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    public ProblemDetail resolve(Exception ex, WebRequest request) {
        return resolve(ex, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    public ProblemDetail resolve(Exception ex, HttpStatusCode statusCode, WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(
                ex,
                statusCode,
                ex.getMessage(),
                null,
                null,
                PlayHubErrorCodes.VALIDATION_ERROR_CODE,
                request.getLocale()
        );
        enrich(problemDetail, ex, request);
        return problemDetail;
    }

    @Override
    public void enrich(ProblemDetail problemDetail, Exception ex, WebRequest request) {
        Object validationDetails = null;
        if (ex instanceof BindException be) {
            validationDetails = createValidationProperty(be, request.getLocale());
        } else if (ex instanceof MethodValidationException mav) {
            validationDetails = createValidationProperty(mav, request.getLocale());
        }
        problemDetail.setProperty(VALIDATION_DETAILS_NAME, validationDetails);
        this.initErrorCodeIfAbsent(problemDetail, PlayHubErrorCodes.VALIDATION_ERROR_CODE);
    }

    @Override
    public Set<Class<? extends Throwable>> resolvableExceptions() {
        return Set.of(BindException.class, MethodValidationException.class);
    }

    private Object createValidationProperty(BindException ex, Locale locale) {
        return createMapFieldsErrors(ex.getFieldErrors(), locale);
    }

    private Object createValidationProperty(MethodValidationException ex, Locale locale) {
        Map<String, Map<String, List<String>>> objectsFieldsErrors = ex.getBeanResults().stream()
                .map(br -> {
                    String objectName = br.getObjectName().substring(br.getObjectName().lastIndexOf('.') + 1);
                    Map<String, List<String>> map = createMapFieldsErrors(br.getFieldErrors(), locale);
                    return Map.entry(objectName, map);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (objectsFieldsErrors.size() == 1) {
            return objectsFieldsErrors.values().iterator().next();
        }
        return objectsFieldsErrors;
    }

    private Map<String, List<String>> createMapFieldsErrors(Collection<? extends FieldError> fieldErrors,
                                                            Locale locale) {
        Map<String, List<String>> map = new HashMap<>();
        fieldErrors.forEach(error -> {
            String fieldName = error.getField();
            String message = resolveMessage(error, locale);
            map.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(message);
        });
        return map;
    }


}
