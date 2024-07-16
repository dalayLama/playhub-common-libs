package com.playhub.autoconfigure.security.rest.secutiry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayHubUser implements Principal {

    @Getter
    private final UUID id;

    private final String name;

    @Override
    public String getName() {
        return name;
    }

}
