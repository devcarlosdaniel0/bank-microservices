package com.marchesin.account.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeyCloakManager {

    private final KeyCloakProperties keyCloakProperties;

    public RealmResource getKeyCloakInstanceWithRealm() {

        return keyCloakProperties.keycloakInstance().realm(keyCloakProperties.getRealm());
    }
}
