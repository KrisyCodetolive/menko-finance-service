package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.LoginRequest;
import com.menko.comptabilite.application.dto.request.UtilisateurRequest;
import com.menko.comptabilite.application.dto.response.TokenResponse;
import com.menko.comptabilite.application.port.in.AuthUseCase;
import com.menko.comptabilite.domain.model.Utilisateur;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Authentification", description = "Connexion JWT et gestion des utilisateurs (SUPER_ADMIN)")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @Operation(summary = "Connexion", description = "Authentifie l'utilisateur et retourne un token JWT valide 24h")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authUseCase.connecter(request)));
    }

    @Operation(summary = "Créer un utilisateur", description = "Crée un compte utilisateur. Accès réservé au SUPER_ADMIN")
    @PostMapping("/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Utilisateur>> creerUtilisateur(
            @Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(authUseCase.creerUtilisateur(request)));
    }

    @Operation(summary = "Modifier un utilisateur", description = "Met à jour les informations d'un utilisateur existant. Accès réservé au SUPER_ADMIN")
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Utilisateur>> modifierUtilisateur(
            @PathVariable UUID id,
            @Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authUseCase.modifierUtilisateur(id, request)));
    }

    @Operation(summary = "Désactiver un utilisateur", description = "Désactive le compte d'un utilisateur. Accès réservé au SUPER_ADMIN")
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactiverUtilisateur(@PathVariable UUID id) {
        authUseCase.desactiverUtilisateur(id);
        return ResponseEntity.ok(ApiResponse.ok("Utilisateur désactivé"));
    }

    @Operation(summary = "Lister les utilisateurs", description = "Retourne la liste de tous les comptes utilisateurs. Accès réservé au SUPER_ADMIN")
    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Utilisateur>>> listerUtilisateurs() {
        return ResponseEntity.ok(ApiResponse.ok(authUseCase.listerUtilisateurs()));
    }
}
