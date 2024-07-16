package com.playhub.common.exceptions;

import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public abstract class PlayhubException extends RuntimeException implements ErrorResponse, PlayhubErrorCodeResolvable {

    private final ProblemDetail problemDetail;

    public PlayhubException() {
        this.problemDetail = ProblemDetail.forStatus(getStatusCode());
    }

    public PlayhubException(String message) {
        super(message);
        this.problemDetail = ProblemDetail.forStatusAndDetail(getStatusCode(), getMessage());
    }

    public PlayhubException(String message, Throwable cause) {
        super(message, cause);
        this.problemDetail = ProblemDetail.forStatusAndDetail(getStatusCode(), getMessage());
    }

    public PlayhubException(Throwable cause) {
        super(cause);
        this.problemDetail = ProblemDetail.forStatus(getStatusCode());
    }

    public PlayhubException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.problemDetail = ProblemDetail.forStatusAndDetail(getStatusCode(), getMessage());
    }

    @Override
    public ProblemDetail getBody() {
        return problemDetail;
    }

    @Override
    public abstract Object[] getDetailMessageArguments();

    @Override
    public final String getTitleMessageCode() {
        return getMessageSourceResolvableTitle();
    }

    @Override
    public final String getDetailMessageCode() {
        return getMessageSourceResolvableDetailMessage();
    }

    public abstract String getMessageSourceResolvableTitle();

    public abstract String getMessageSourceResolvableDetailMessage();

}
