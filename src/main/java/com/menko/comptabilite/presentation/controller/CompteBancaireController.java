package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.CompteBancaireRequest;
import com.menko.comptabilite.application.port.in.CompteBancaireUseCase;
import com.menko.comptabilite.domain.model.CompteBancaire;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Comptes bancaires", description = "Gestion des comptes bancaires des filiales")
@RestController
@RequestMapping("/api/v1/comptes-bancaires")
@RequiredArgsConstructor
public class CompteBancaireController {

    private final CompteBancaireUseCase compteBancaireUseCase;

    @Operation(summary = "Lister les comptes", description = "Comptes bancaires actifs d'une filiale")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CompteBancaire>>> lister(@RequestParam UUID filialeId) {
        return ResponseEntity.ok(ApiResponse.ok(
                compteBancaireUseCase.listerParFiliale(filialeId)));
    }

    @Operation(summary = "Obtenir un compte", description = "Retourne les détails d'un compte bancaire")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompteBancaire>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(compteBancaireUseCase.trouverParId(id)));
    }

    @Operation(summary = "Ouvrir un compte", description = "Crée un nouveau compte bancaire pour une filiale")
    @PostMapping
    public ResponseEntity<ApiResponse<CompteBancaire>> creer(
            @Valid @RequestBody CompteBancaireRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.created(compteBancaireUseCase.creer(request)));
    }

    @Operation(summary = "Modifier un compte", description = "Met à jour les informations d'un compte bancaire")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompteBancaire>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody CompteBancaireRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(compteBancaireUseCase.modifier(id, request)));
    }

    @Operation(summary = "Clôturer un compte", description = "Clôture définitivement un compte bancaire. Action irréversible")
    @PostMapping("/{id}/cloturer")
    public ResponseEntity<ApiResponse<Void>> cloturer(@PathVariable UUID id) {
        compteBancaireUseCase.cloturerCompte(id);
        return ResponseEntity.ok(ApiResponse.ok("Compte bancaire clôturé"));
    }
}
