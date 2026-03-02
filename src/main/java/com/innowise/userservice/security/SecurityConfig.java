package com.innowise.userservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Security configuration using Keycloak JWT tokens via Spring's
 * oauth2ResourceServer. Replaces the old custom JwtAuthenticationFilter.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${keycloak.resource}")
    private String resourceName;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://keycloak:8080/realms/innowise-realm}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                        .requestMatchers("/api/users/internal/**").hasAuthority("SCOPE_internal-service")
                        .requestMatchers("/api/internal/**").hasAuthority("SCOPE_internal-service")
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/cards/**").permitAll()
                        .anyRequest().hasAuthority("ROLE_ADMIN"))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder).jwtAuthenticationConverter(keycloakJwtConverter())));

        return http.build();
    }

    /**
     * Custom JwtDecoder that uses JWK Set URI directly to bypass strict issuer
     * hostname check.
     * Keycloak reports issuer as 'localhost' but services connect via 'keycloak'
     * hostname.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = issuerUri + "/protocol/openid-connect/certs";
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator validator = new JwtTimestampValidator(Duration.ofSeconds(60));
        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }

    /**
     * Extracts realm roles and scopes from Keycloak JWT.
     * Roles are mapped to ROLE_* and scopes to SCOPE_*
     */
    private JwtAuthenticationConverter keycloakJwtConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new java.util.ArrayList<>();

            org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter scopeConverter = new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
            authorities.addAll(scopeConverter.convert(jwt));

            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            log.info("JWT realm_access claim: {}", realmAccess);


            if (realmAccess != null && realmAccess.get("roles") instanceof List<?> list) {
                list.stream()
                        .filter(r -> r instanceof String)
                        .map(r -> (String) r)
                        .map(String::toUpperCase)
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
                log.info("Added roles from JWT: {}", realmAccess.get("roles"));
            }

            log.info("Final Mapped authorities: {}",
                    authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

            return authorities;
        });
        converter.setPrincipalClaimName("email");
        return converter;
    }
}
