package com.playhub.security.rest.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Slf4j
public class DefaultJwdDecoder implements JwtDecoder {

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            Instant issueTime = Optional.ofNullable(claimsSet.getIssueTime()).map(Date::toInstant).orElse(null);
            Instant expirationTime = Optional.ofNullable(claimsSet.getExpirationTime()).map(Date::toInstant).orElse(null);
            return Jwt.withTokenValue(token)
                    .headers(target -> target.putAll(jwt.getHeader().toJSONObject()))
                    .claims(target -> target.putAll(claimsSet.toJSONObject()))
                    .issuedAt(issueTime)
                    .expiresAt(expirationTime)
                    .build();
        } catch (ParseException e) {
            throw new JwtException("Couldn't pars the JWT token", e);
        }
    }

}
