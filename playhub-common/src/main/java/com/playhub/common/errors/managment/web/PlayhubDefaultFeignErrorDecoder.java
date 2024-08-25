package com.playhub.common.errors.managment.web;

import com.playhub.common.errors.exceptions.PlayhubFeignClientException;
import com.playhub.common.errors.exceptions.UnreadableRestClientException;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayhubDefaultFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        Response.Body body = response.body();
        HttpStatus status = Optional.ofNullable(HttpStatus.resolve(response.status()))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        if (body == null || body.length() == null) {
            return toUnreadableRestClientException(status, response);
        }

        try {
            return toPlayhubFeignClientException(status, response);
        } catch (IOException e) {
            return toUnreadableRestClientException(e, status, response);
        }
    }

    protected PlayhubFeignClientException toPlayhubFeignClientException(
            HttpStatus status,
            Response response) throws IOException {
        byte[] bytes = response.body().asInputStream().readAllBytes();
        return new PlayhubFeignClientException(status, bytes);
    }

    protected UnreadableRestClientException toUnreadableRestClientException(HttpStatus httpStatus, Response response) {
        var feignClientException = new FeignException.FeignClientException(
                httpStatus.value(), "Unreadable error", response.request(), new byte[0], response.headers()
        );
        return toUnreadableRestClientException(feignClientException, httpStatus, response);
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
