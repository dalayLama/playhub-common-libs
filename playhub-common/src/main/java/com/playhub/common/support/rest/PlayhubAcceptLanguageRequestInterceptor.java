package com.playhub.common.support.rest;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class PlayhubAcceptLanguageRequestInterceptor implements RequestInterceptor {

    private final Supplier<Locale> localeSupplier;

    public PlayhubAcceptLanguageRequestInterceptor() {
        this(LocaleContextHolder::getLocale);
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("Accept-Language", localeSupplier.get().toLanguageTag());
    }

}
