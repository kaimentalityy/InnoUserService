package com.innowise.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Value("${swagger.keycloak.token-url:http://localhost:8088/realms/innowise-realm/protocol/openid-connect/token}")
        private String tokenUrl;

        @Value("${swagger.keycloak.auth-url:http://localhost:8088/realms/innowise-realm/protocol/openid-connect/auth}")
        private String authUrl;

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("User Service API")
                                                .version("1.0")
                                                .description("Documentation for User Service API (including User and Card management)"))
                                .servers(List.of(
                                                new Server().url("/")
                                                                .description("Default Server URL (Gateway Relative)")))
                                .addSecurityItem(new SecurityRequirement().addList("keycloak"))
                                .components(new Components()
                                                .addSecuritySchemes("keycloak", new SecurityScheme()
                                                                .type(SecurityScheme.Type.OAUTH2)
                                                                .description("Authenticate via Keycloak (Authorization Code flow)")
                                                                .flows(new OAuthFlows()
                                                                                .authorizationCode(new OAuthFlow()
                                                                                                .authorizationUrl(
                                                                                                                authUrl)
                                                                                                .tokenUrl(tokenUrl)
                                                                                                .refreshUrl(tokenUrl)
                                                                                                .scopes(new Scopes()
                                                                                                                .addString("openid",
                                                                                                                                "OpenID Connect scope")
                                                                                                                .addString("profile",
                                                                                                                                "User profile scope")
                                                                                                                .addString("email",
                                                                                                                                "User email scope"))))));
        }

}
