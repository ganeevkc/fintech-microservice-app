package com.finverse.apiGateway.filter;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.io.ObjectInputFilter;
import java.util.List;

@Component
public class JwtValidationFilter extends AbstractGatewayFilterFactory {

    private final JwtParser jwtParser;

    @Value("${jwt.secret}")
    private String secret;

    public JwtValidationFilter() {
        super(ObjectInputFilter.Config.class);
        this.jwtParser = Jwts.parser()
                .setSigningKey(secret.getBytes()) // Replace with env variable
                .build();
    }
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token = extractToken(exchange.getRequest());

            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                Claims claims = jwtParser.parseClaimsJws(token).getBody();

                // Add user info to headers
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header("X-User-ID", claims.getSubject())
                        .header("X-User-Roles", String.valueOf(claims.get("roles")))
                        .build();

                return chain.filter(exchange.mutate().request(request).build());
            } catch (JwtException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().get("Authorization");
        if (headers == null || headers.isEmpty()) return null;

        String bearerToken = headers.get(0);
        if (bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static class Config {
        // Add configuration properties if needed
    }
}
