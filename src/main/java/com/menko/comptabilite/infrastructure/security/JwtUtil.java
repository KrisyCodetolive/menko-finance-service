package com.menko.comptabilite.infrastructure.security;

import com.menko.comptabilite.domain.model.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    @Getter
    private final long expirationMs;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String genererToken(Utilisateur utilisateur) {
        return Jwts.builder()
                .subject(utilisateur.getIdentifiant().toString())
                .claim("role", utilisateur.getNomRole())
                .claim("email", utilisateur.getEmail())
                .claim("filialeId", utilisateur.getIdentifiantFiliale() != null
                        ? utilisateur.getIdentifiantFiliale().toString() : null)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Claims extraireClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean estValide(String token) {
        try {
            extraireClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extraireUserId(String token) {
        return extraireClaims(token).getSubject();
    }

    public String extraireRole(String token) {
        return extraireClaims(token).get("role", String.class);
    }
}
