package com.playhub.security.rest.support;

import com.playhub.security.rest.PlayHubAuthenticationToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class PlayhubAuthorizationRequestInterceptor implements RequestInterceptor {

    private final Set<String> urlPatterns;

    public PlayhubAuthorizationRequestInterceptor() {
        this(Collections.emptySet());
    }

    @Override
    public void apply(RequestTemplate template) {
        if (!isMatched(template)) {
            return;
        }

        getToken().ifPresent(token -> template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }

    private Optional<String> getToken() {
        var authentication = (PlayHubAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
                .map(auth -> ((Jwt) authentication.getCredentials()))
                .map(AbstractOAuth2Token::getTokenValue);
    }

    private boolean isMatched(RequestTemplate template) {
        if (urlPatterns.isEmpty()) {
            return true;
        }
        return urlPatterns.stream().anyMatch(pattern -> template.url().matches(pattern));
    }

}
