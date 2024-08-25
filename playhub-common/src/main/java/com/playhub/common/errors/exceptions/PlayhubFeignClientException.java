package com.playhub.common.errors.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
public class PlayhubFeignClientException extends RuntimeException {

    private final HttpStatusCode statusCode;

    private final byte[] body;

}
