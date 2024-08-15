package com.playhub.common.errors.managment.web;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;

public class DefaultProblemDetailResolver extends AbstractProblemDetailResolver implements ProblemDetailResolver {

    public DefaultProblemDetailResolver(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    public ProblemDetail resolve(Exception ex, WebRequest request) {
        return resolve(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    public ProblemDetail resolve(Exception ex, HttpStatusCode statusCode, WebRequest request) {
        return createProblemDetail(
                ex,
                statusCode,
                ex.getMessage(),
                null,
                null,
                request.getLocale()
        );
    }

    @Override
    public void enrich(ProblemDetail problemDetail, Exception ex, WebRequest request) {
        initErrorCodeIfAbsent(problemDetail, ex);
    }

}
