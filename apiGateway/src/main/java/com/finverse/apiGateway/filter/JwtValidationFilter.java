package com.finverse.apiGateway.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtValidationFilter extends AbstractGatewayFilterFactory<JwtValidationFilter.Config> {

    private final JwtParser jwtParser;

    public JwtValidationFilter(@Value("${jwt.secret}") String secret) {
        super(Config.class);
        // Create secret key properly for JJWT 0.12.x
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = extractToken(exchange.getRequest());

            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                Claims claims = jwtParser.parseSignedClaims(token).getPayload();

                // Extract userId from 'sub' claim and role from 'role' claim
                String userId = claims.getSubject(); // This is the userId we set in AuthService
                String role = claims.get("role", String.class);
                String username = claims.get("username", String.class);

                // Add user info to headers for downstream services
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header("X-User-ID", userId)
                        .header("X-User-Role", role)
                        .header("X-Username", username)
                        .build();

                return chain.filter(exchange.mutate().request(request).build());

            } catch (JwtException e) {
                System.err.println("JWT validation failed: " + e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().get("Authorization");
        if (headers == null || headers.isEmpty()) return null;

        String bearerToken = headers.get(0);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static class Config {
        // Configuration properties if needed
    }
}