package com.playhub.autoconfigure.security.secutiry;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PlayHubAuthenticationToken extends AbstractAuthenticationToken {

    private final Object credentials;

    private final PlayHubUser user;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public PlayHubAuthenticationToken(Object credentials,
                                      PlayHubUser user,
                                      Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true);
        this.credentials = credentials;
        this.user = user;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

}
