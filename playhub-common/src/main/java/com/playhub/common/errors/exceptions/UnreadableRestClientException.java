package com.playhub.common.errors.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;

import java.util.Locale;

@RequiredArgsConstructor
public class UnreadableRestClientException extends RuntimeException implements ErrorResponse {

    private final ErrorResponse delegate;

    @Override
    public HttpStatusCode getStatusCode() {
        return delegate.getStatusCode();
    }

    @Override
    public HttpHeaders getHeaders() {
        return delegate.getHeaders();
    }

    @Override
    public ProblemDetail getBody() {
        return delegate.getBody();
    }

    @Override
    public String getTypeMessageCode() {
        return delegate.getTypeMessageCode();
    }

    @Override
    public String getTitleMessageCode() {
        return delegate.getTitleMessageCode();
    }

    @Override
    public String getDetailMessageCode() {
        return delegate.getDetailMessageCode();
    }

    @Override
    @Nullable
    public Object[] getDetailMessageArguments() {
        return delegate.getDetailMessageArguments();
    }

    @Override
    @Nullable
    public Object[] getDetailMessageArguments(MessageSource messageSource, Locale locale) {
        return delegate.getDetailMessageArguments(messageSource, locale);
    }

    @Override
    public ProblemDetail updateAndGetBody(MessageSource messageSource, Locale locale) {
        return delegate.updateAndGetBody(messageSource, locale);
    }

}
