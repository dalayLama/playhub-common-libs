package com.playhub.common.errors.managment.web;

import com.playhub.common.errors.exceptions.PlayhubFeignClientException;
import com.playhub.common.errors.exceptions.UnreadableRestClientException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PlayhubDefaultFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception ex = defaultDecoder.decode(methodKey, response);

        if (ex instanceof RetryableException) {
            return ex;
        }

        Response.Body body = response.body();
        HttpStatus status = Optional.ofNullable(HttpStatus.resolve(response.status()))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        if (body == null || body.length() == null) {
            return toUnreadableRestClientException(ex, status, response);
        }

        try {
            return toPlayhubFeignClientException(status, response);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return toUnreadableRestClientException(ex, status, response);
        }
    }

    protected PlayhubFeignClientException toPlayhubFeignClientException(
            HttpStatus status,
            Response response) throws IOException {
        byte[] bytes = response.body().asInputStream().readAllBytes();
        return new PlayhubFeignClientException(status, bytes);
    }

    protected UnreadableRestClientException toUnreadableRestClientException(Exception ex,
                                                                            HttpStatus httpStatus,
                                                                            Response response) {
        URI uri = URI.create(response.request().url());
        ErrorResponse errorResponse = ErrorResponse.builder(ex, httpStatus, "Unreadable error")
                .instance(uri)
                .headers(httpHeaders -> response.headers().forEach((name, values) -> {
                    List<String> listValues = new ArrayList<>(values);
                    httpHeaders.addAll(name, listValues);
                }))
                .build();
        return new UnreadableRestClientException(errorResponse);
    }

}
