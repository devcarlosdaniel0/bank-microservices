package com.marchesin.account.service;

import com.marchesin.account.dto.AuthenticatedUser;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KeycloakService {

    private final KeyCloakManager keyCloakManager;

    public KeycloakService(KeyCloakManager keyCloakManager) {
        this.keyCloakManager = keyCloakManager;
    }

    public Optional<AuthenticatedUser> findByEmail(String email) {

        List<UserRepresentation> users =
                keyCloakManager
                        .getKeyCloakInstanceWithRealm()
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
