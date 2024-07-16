package com.playhub.common.web.error.managment;

import org.springframework.context.MessageSource;
import org.springframework.core.ExceptionDepthComparator;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProblemDetailResolverManager extends AbstractProblemDetailResolver implements ProblemDetailResolver {

    private final ProblemDetailResolver defaultResolver;

    private final Map<Class<? extends Throwable>, ? extends ProblemDetailResolver> resolvers;

    public ProblemDetailResolverManager(MessageSource messageSource,
                                        ProblemDetailResolver defaultResolver,
                                        Map<Class<? extends Throwable>, ? extends ProblemDetailResolver> resolvers) {
        super(messageSource);
        this.defaultResolver = defaultResolver;
        this.resolvers = resolvers;
    }


    @Override
    public ProblemDetail resolve(Exception ex, WebRequest request) {
        if (ex instanceof ErrorResponse er) {
            ProblemDetail problemDetail = er.updateAndGetBody(getMessageSource(), request.getLocale());
            enrich(problemDetail, ex, request);
            return problemDetail;
        }
        ProblemDetailResolver resolver = findResolver(ex.getClass());
        ProblemDetail problemDetail = resolver.resolve(ex, request);
        initErrorCodeIfAbsent(problemDetail, ex);
        return problemDetail;
    }

    @Override
    public ProblemDetail resolve(Exception ex, HttpStatusCode statusCode, WebRequest request) {
        if (ex instanceof ErrorResponse er) {
            ProblemDetail problemDetail = er.updateAndGetBody(getMessageSource(), request.getLocale());
            enrich(problemDetail, ex, request);
            return problemDetail;
        }
        ProblemDetailResolver resolver = findResolver(ex.getClass());
        ProblemDetail problemDetail = resolver.resolve(ex, statusCode, request);
        initErrorCodeIfAbsent(problemDetail, ex);
        return problemDetail;
    }

    @Override
    public void enrich(ProblemDetail problemDetail, Exception ex, WebRequest request) {
        ProblemDetailResolver resolver = findResolver(ex.getClass());
        resolver.enrich(problemDetail, ex, request);
        initErrorCodeIfAbsent(problemDetail, ex);
    }

    private ProblemDetailResolver findResolver(Class<? extends Throwable> exceptionType) {
        List<Class<? extends Throwable>> matches = new ArrayList<>();
        for (Class<? extends Throwable> mappedException : this.resolvers.keySet()) {
            if (mappedException.isAssignableFrom(exceptionType)) {
                matches.add(mappedException);
            }
        }
        if (!matches.isEmpty()) {
            if (matches.size() > 1) {
                matches.sort(new ExceptionDepthComparator(exceptionType));
            }
            return this.resolvers.get(matches.get(0));
        }
        return this.defaultResolver;
    }

}
