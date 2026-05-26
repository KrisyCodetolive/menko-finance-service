package com.menko.comptabilite.application.dto.response;

import java.util.UUID;

public record TokenResponse(
        String token,
        String type,
        UUID utilisateurId,
        String email,
        String role,
        UUID filialeId,
        long expirationMs
) {
    public static TokenResponse bearer(String token, UUID userId, String email, String role, UUID filialeId, long expiration) {
        return new TokenResponse(token, "Bearer", userId, email, role, filialeId, expiration);
    }
}
