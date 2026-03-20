package com.marchesin.user.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KeycloakProperties {

    @Value("${app.config.keycloak.server-url}")
    private String serverUrl;

    @Value("${app.config.keycloak.realm}")
    private String realm;

    @Value("${app.config.keycloak.client-id}")
    private String clientId;

    @Value("${app.config.keycloak.client-secret}")
    private String clientSecret;
}
