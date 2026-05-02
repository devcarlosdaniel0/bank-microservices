package com.marchesin.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.config.gateway-url}")
    private String gatewayUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("oauth2"))
                .components(new Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("http://localhost:8181/realms/bank-realm/protocol/openid-connect/auth")
                                                .tokenUrl("http://localhost:8181/realms/bank-realm/protocol/openid-connect/token")
                                                .scopes(new Scopes()
                                                        .addString("openid", "openid")
                                                        .addString("profile", "profile")
                                                        .addString("email", "email"))))))
                .servers(List.of(new Server().url(gatewayUrl).description("Gateway")));
    }
}