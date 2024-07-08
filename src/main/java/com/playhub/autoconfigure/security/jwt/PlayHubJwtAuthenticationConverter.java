package com.playhub.autoconfigure.security.jwt;

import com.playhub.autoconfigure.security.secutiry.PlayHubAuthenticationToken;
import com.playhub.autoconfigure.security.secutiry.PlayHubUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.UUID;

public class PlayHubJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String principalClaimName;

    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter;

    public PlayHubJwtAuthenticationConverter(String principalClaimName,
                                             Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter) {
        this.principalClaimName = principalClaimName;
        this.jwtGrantedAuthoritiesConverter = authoritiesConverter;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        String name = source.getClaimAsString(principalClaimName);
        UUID userId = UUID.fromString(source.getSubject());
        Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(source);
        PlayHubUser playHubUser = new PlayHubUser(userId, name);
        return new PlayHubAuthenticationToken(source, playHubUser, authorities);
    }

}
