package com.playhub.common.web.error.managment;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;

public interface ProblemDetailResolver {

    ProblemDetail resolve(Exception ex, WebRequest request);

    ProblemDetail resolve(Exception ex, HttpStatusCode statusCode, WebRequest request);

    void enrich(ProblemDetail problemDetail, Exception ex, WebRequest request);

}
