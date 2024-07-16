package com.playhub.common.web.error.managment;

import com.playhub.common.exceptions.PlayHubErrorCodes;
import com.playhub.common.exceptions.PlayhubErrorCodeResolvable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;

import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public abstract class AbstractProblemDetailResolver implements ProblemDetailResolver {

    private final MessageSource messageSource;

    protected String resolveMessage(MessageSourceResolvable resolvable, Locale locale) {
        return Optional.ofNullable(getMessageSource())
                .map(messageSource -> messageSource.getMessage(resolvable, locale))
                .orElseGet(resolvable::getDefaultMessage);
    }

    protected ProblemDetail createProblemDetail(
            Exception ex,
            HttpStatusCode status,
            String defaultDetail,
            @Nullable String detailMessageCode,
            @Nullable Object[] detailMessageArguments,
            Locale locale) {
        return createProblemDetail(
                ex,
                status,
                defaultDetail,
                detailMessageCode,
                detailMessageArguments,
                PlayHubErrorCodes.UNDEFINED_ERROR_CODE,
                locale
        );
    }

    protected ProblemDetail createProblemDetail(
            Exception ex,
            HttpStatusCode status,
            String defaultDetail,
            @Nullable String detailMessageCode,
            @Nullable Object[] detailMessageArguments,
            String errorCode,
            Locale locale) {

        ErrorResponse.Builder builder = ErrorResponse.builder(ex, status, defaultDetail);
        if (detailMessageCode != null) {
            builder.detailMessageCode(detailMessageCode);
        }
        if (detailMessageArguments != null) {
            builder.detailMessageArguments(detailMessageArguments);
        }
        ProblemDetail problemDetail = builder.build().updateAndGetBody(getMessageSource(), locale);
        initErrorCode(problemDetail, errorCode);
        return problemDetail;
    }

    protected void initErrorCode(ProblemDetail problemDetail, Exception ex) {
        problemDetail.setProperty(PlayHubErrorCodes.ERROR_CODE_FIELD_NAME, defineErrorCode(ex));
    }

    protected void initErrorCode(ProblemDetail problemDetail, String errorCode) {
        problemDetail.setProperty(PlayHubErrorCodes.ERROR_CODE_FIELD_NAME, errorCode);
    }

    protected void initErrorCodeIfAbsent(ProblemDetail problemDetail, Exception ex) {
        initErrorCodeIfAbsent(problemDetail, defineErrorCode(ex));
    }

    protected void initErrorCodeIfAbsent(ProblemDetail problemDetail, String errorCode) {
        Optional.ofNullable(problemDetail.getProperties())
                        .ifPresentOrElse(
                                props -> props.putIfAbsent(PlayHubErrorCodes.ERROR_CODE_FIELD_NAME, errorCode),
                                () -> problemDetail.setProperty(PlayHubErrorCodes.ERROR_CODE_FIELD_NAME, errorCode)
                        );
    }

    protected String defineErrorCode(Exception ex) {
        if (ex instanceof PlayhubErrorCodeResolvable cr) {
            return cr.getErrorCode();
        }
        return PlayHubErrorCodes.UNDEFINED_ERROR_CODE;
    }

}
