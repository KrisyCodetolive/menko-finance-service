package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.LoginRequest;
import com.menko.comptabilite.application.dto.request.UtilisateurRequest;
import com.menko.comptabilite.application.dto.response.TokenResponse;
import com.menko.comptabilite.application.port.in.AuthUseCase;
import com.menko.comptabilite.domain.model.Utilisateur;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authUseCase.connecter(request)));
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Utilisateur>> creerUtilisateur(
            @Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(authUseCase.creerUtilisateur(request)));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Utilisateur>> modifierUtilisateur(
            @PathVariable UUID id,
            @Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authUseCase.modifierUtilisateur(id, request)));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactiverUtilisateur(@PathVariable UUID id) {
        authUseCase.desactiverUtilisateur(id);
        return ResponseEntity.ok(ApiResponse.ok("Utilisateur désactivé"));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Utilisateur>>> listerUtilisateurs() {
        return ResponseEntity.ok(ApiResponse.ok(authUseCase.listerUtilisateurs()));
    }
}
