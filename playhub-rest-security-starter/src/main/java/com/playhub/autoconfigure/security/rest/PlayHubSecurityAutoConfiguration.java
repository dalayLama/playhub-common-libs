package com.playhub.autoconfigure.security.rest;

import com.playhub.security.rest.consts.KeycloakJwtClaimNames;
import com.playhub.security.rest.jwt.DefaultJwdDecoder;
import com.playhub.security.rest.jwt.PlayHubJwtAuthenticationConverter;
import com.playhub.security.rest.jwt.PlayHubJwtGrantedAuthoritiesConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@AutoConfiguration
@Slf4j
public class PlayHubSecurityAutoConfiguration {

    @Bean
    @ConditionalOnClass(name = "io.swagger.v3.oas.models.OpenAPI")
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring open-api Security Filter Chain");
        return http
            .securityMatcher("/v3/api-docs/**", "/swagger-ui/**", "swagger-ui.html")
            .authorizeHttpRequests(registry -> registry.anyRequest().permitAll())
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ehc ->
                ehc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring root Security Filter Chain");
        http.oauth2ResourceServer(customizer ->
                customizer.jwt(jwtCustomizer ->
                        jwtCustomizer.jwtAuthenticationConverter(jwtAuthenticationConverter())
                ));
        return http
                .authorizeHttpRequests(registry -> registry.anyRequest().authenticated())
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ehc ->
                        ehc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        log.info("Creating Jwt Decoder");
        return new DefaultJwdDecoder();
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        PlayHubJwtGrantedAuthoritiesConverter authoritiesConverter = new PlayHubJwtGrantedAuthoritiesConverter(
                KeycloakJwtClaimNames.ROLES
        );
        return new PlayHubJwtAuthenticationConverter(KeycloakJwtClaimNames.PREFERRED_USERNAME, authoritiesConverter);
    }

}
