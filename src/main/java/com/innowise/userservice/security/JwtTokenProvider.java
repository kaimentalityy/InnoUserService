package com.innowise.userservice.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObj = claims.get("roles");
        if (rolesObj == null) {
            rolesObj = claims.get("authorities");
        }
        if (rolesObj == null) {
            rolesObj = claims.get("role");
        }

        if (rolesObj instanceof List<?> list && !list.isEmpty()) {
            Object first = list.getFirst();
            if (first instanceof String s) {
                return s;
            }
            if (first instanceof java.util.Map<?, ?> map) {
                Object auth = map.get("authority");
                return auth != null ? auth.toString() : "";
            }
        } else if (rolesObj instanceof String s) {
            return s;
        }

        return "";
    }

}
