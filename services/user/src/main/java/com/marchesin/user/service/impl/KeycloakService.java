package com.marchesin.user.service.impl;

import com.marchesin.user.config.KeycloakProperties;
import com.marchesin.user.domain.AuthenticatedUser;
import com.marchesin.user.service.IdentityProvider;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KeycloakService implements IdentityProvider {

    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    public KeycloakService(Keycloak keycloak, KeycloakProperties properties) {
        this.keycloak = keycloak;
        this.properties = properties;
    }

    @Override
    public Optional<AuthenticatedUser> findByEmail(String email) {

        List<UserRepresentation> users = keycloak.realm(properties.getRealm())
                        .users()
                        .searchByEmail(email, true);

        if (users.isEmpty()) {
            return Optional.empty();
        }

        UserRepresentation user = users.get(0);

        return Optional.of(new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                Boolean.TRUE.equals(user.isEmailVerified())
        ));
    }
}
