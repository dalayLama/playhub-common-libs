package com.playhub.common.errors.managment.web.resolvers;

import com.playhub.common.errors.managment.PlayHubErrorCodes;
import com.playhub.common.errors.managment.web.AbstractProblemDetailResolver;
import com.playhub.common.errors.managment.web.TypeAwareProblemDetailResolver;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;

public class AccessDeniedProblemDetailResolver extends AbstractProblemDetailResolver
        implements TypeAwareProblemDetailResolver {

    public AccessDeniedProblemDetailResolver(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    public ProblemDetail resolve(Exception ex, WebRequest request) {
        return resolve(ex, HttpStatus.FORBIDDEN, request);
    }

    @Override
    public ProblemDetail resolve(Exception ex, HttpStatusCode statusCode, WebRequest request) {
        ProblemDetail problemDetail = ErrorResponse.builder(ex, statusCode, ex.getMessage())
                .titleMessageCode("AccessDenied.title")
                .detailMessageCode("AccessDenied.message")
                .build()
                .updateAndGetBody(getMessageSource(), request.getLocale());
        enrich(problemDetail, ex, request);
        return problemDetail;
    }

    @Override
    public void enrich(ProblemDetail problemDetail, Exception ex, WebRequest request) {
        this.initErrorCodeIfAbsent(problemDetail, PlayHubErrorCodes.ACCESS_DENIED_ERROR_CODE);
    }

    @Override
    public Set<Class<? extends Throwable>> resolvableExceptions() {
        return Set.of(AccessDeniedException.class);
    }

}
