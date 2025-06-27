package com.finverse.auth.service;


import com.finverse.auth.repository.TokenService;
import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@Component
public class JWTTokenService implements TokenService, Clock {

    private static final String DOT = ".";

    private final String issuer;
    private final int expirationInSec;
    private final int clockSkewSec;
    private final Key secretKey;

    public JWTTokenService(@Value("${jwt.issuer}") String issuer,
                           @Value("${jwt.expiration}") int expirationInSec,
                           @Value("${jwt.clock-skew}") int clockSkewSec,
                           @Value("${jwt.secret}") String secretKey) {
        this.issuer = issuer;
        this.expirationInSec = expirationInSec;
        this.clockSkewSec = clockSkewSec;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String permanent(Map<String, String> attributes) {
        return newToken(attributes, 0);
    }

    @Override
    public String expiring(Map<String, String> attributes) {
        return newToken(attributes, expirationInSec);
    }

    @Override
    public Map<String, String> untrusted(String token) {
        String noSignature = substringBeforeLast(token, DOT) + DOT;
        return parseClaims(() -> Jwts.parser()
                .unsecured()
                .clock(this)
                .build()
                .parseClaimsJwt(noSignature)
                .getPayload());
    }

    @Override
    public Map<String, String> verify(String token) {
        return parseClaims(() -> Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .clock(this)
                .build()
                .parseSignedClaims(token)
                .getPayload());
    }

//    @Override
//    public Instant now() {
//        return Instant.now();
//    }

    private String newToken(final Map<String, String> attributes, final int expirationInSec) {
        Instant now = Instant.now();
        Instant expiration = (expirationInSec > 0) ?
                now.plusSeconds(expirationInSec) : null;

        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(expiration != null ? Date.from(expiration) : null)
                .claims(attributes)  // All custom claims at once
                .signWith(secretKey)
                .compact();
    }

    private static Map<String, String> parseClaims(Supplier<Claims> toClaims) {
        try {
            Claims claims = toClaims.get();
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            claims.forEach((key, value) -> builder.put(key, String.valueOf(value)));
            return builder.build();
        } catch (IllegalArgumentException | JwtException e) {
            return ImmutableMap.of();
        }
    }

    @Override
    public Date now() {
        return new Date();
    }

//    private JwtParser getParser() {
//        return Jwts
//                .parserBuilder()
//                .requireIssuer(issuer)
//                .setClock(this)
//                .setAllowedClockSkewSeconds(clockSkewSec)
//                .setSigningKey(secretKey)
//                .build();
//    }
}
