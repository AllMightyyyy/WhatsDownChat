package org.example.whatsdownbackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.whatsdownbackend.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtils {

    private final Key key;
    private final long jwtExpirationMs;

    // Simple in-memory token blacklist. For production, use a persistent store like Redis.
    private Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    public JwtUtils(
            @Value("${app.jwtSecret}") String jwtSecret,
            @Value("${app.jwtExpirationMs}") long jwtExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Generates a JWT token based on the authenticated user's details.
     *
     * @param authentication The authentication object.
     * @return The JWT token.
     */
    public String generateJwtToken(Authentication authentication){
        org.springframework.security.core.userdetails.User userPrincipal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Typically the email
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token directly from a User entity.
     *
     * @param user The user entity.
     * @return The JWT token.
     */
    public String generateJwtTokenFromUser(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the user's email from the JWT token.
     *
     * @param token The JWT token.
     * @return The user's email.
     */
    public String getUserEmailFromJwtToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validates the JWT token and checks if it's not revoked.
     *
     * @param authToken The JWT token.
     * @return True if valid and not revoked, false otherwise.
     */
    public boolean validateJwtToken(String authToken){
        if (isTokenRevoked(authToken)) {
            return false;
        }

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e){
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e){
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e){
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e){
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e){
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    /**
     * Adds a token to the blacklist.
     *
     * @param token The JWT token to revoke.
     */
    public void revokeToken(String token) {
        tokenBlacklist.add(token);
    }

    /**
     * Checks if a token is revoked.
     *
     * @param token The JWT token to check.
     * @return True if revoked, false otherwise.
     */
    public boolean isTokenRevoked(String token) {
        return tokenBlacklist.contains(token);
    }
}
