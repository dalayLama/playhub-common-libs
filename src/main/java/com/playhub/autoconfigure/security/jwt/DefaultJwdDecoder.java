package com.playhub.autoconfigure.security.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.text.ParseException;
import java.util.Map;

@Slf4j
public class DefaultJwdDecoder implements JwtDecoder {

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            return Jwt.withTokenValue(token)
                    .headers(target -> target.putAll(jwt.getHeader().toJSONObject()))
                    .claims(target -> target.putAll(claimsSet.toJSONObject()))
                    .build();
        } catch (ParseException e) {
            throw new JwtException("Couldn't pars the JWT token", e);
        }
    }

}
