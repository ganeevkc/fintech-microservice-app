package com.finverse.security.user.service;

import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
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
                           @Value("${jwt.expiration-sec}") int expirationInSec,
                           @Value("${jwt.clock-skew-sec}") int clockSkewSec,
                           @Value("${jwt.secret}") String secretKey) {
        this.issuer = issuer;
        this.expirationInSec = expirationInSec;
        this.clockSkewSec = clockSkewSec;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)); // Ensure your secret is Base64 encoded
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
        final JwtParser parser = getParser();
        final String noSignature = substringBeforeLast(token, DOT) + DOT;
        return parseClaims(() -> parser.parseClaimsJwt(noSignature).getBody()); // parseClaimsJwt not JWS (no signature)
    }

    @Override
    public Map<String, String> verify(String token) {
        return parseClaims(() -> getParser().parseClaimsJws(token).getBody());
    }

    @Override
    public Date now() {
        return new Date();
    }

    private String newToken(final Map<String, String> attributes, final int expirationInSec) {
        final LocalDateTime currentTime = LocalDateTime.now();
        final Claims claims = Jwts
                .claims()
                .setIssuer(issuer)
                .setIssuedAt(Date.from(currentTime.toInstant(ZoneOffset.UTC)));

        if (expirationInSec > 0) {
            final LocalDateTime expiresAt = currentTime.plusSeconds(expirationInSec);
            claims.setExpiration(Date.from(expiresAt.toInstant(ZoneOffset.UTC)));
        }

        claims.putAll(attributes);

        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compressWith(CompressionCodecs.GZIP) // ✅ Use public CompressionCodecs
                .compact();
    }

    private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
        try {
            final Claims claims = toClaims.get();
            final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            claims.forEach((key, value) -> builder.put(key, String.valueOf(value)));
            return builder.build();
        } catch (final IllegalArgumentException | JwtException e) {
            return ImmutableMap.of();
        }
    }

    private JwtParser getParser() {
        return Jwts
                .parserBuilder()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(secretKey)
                .build();
    }
}
