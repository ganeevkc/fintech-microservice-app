package com.finverse.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    // Generate token from user details
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, userDetails.getUsername(), jwtExpirationMs);
    }

    // Generate refresh token
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails.getUsername(), refreshExpirationMs);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .claims(claims)  // New in 0.12.x
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)  // Changed signature
                .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())  // Changed from setSigningKey
                .build()
                .parseSignedClaims(token)  // Changed from parseClaimsJws
                .getPayload();
    }

    // Validate token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Handle various JWT exceptions
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new JwtException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new JwtException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new JwtException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new JwtException("JWT claims string is empty");
        } catch (JwtException ex) {
            throw new JwtException("JWT validation failed");
        }
    }
}