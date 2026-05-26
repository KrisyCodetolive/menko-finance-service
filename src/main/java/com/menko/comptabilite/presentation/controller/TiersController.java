package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.TiersRequest;
import com.menko.comptabilite.application.port.in.TiersUseCase;
import com.menko.comptabilite.domain.model.Tiers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Tiers", description = "Clients, fournisseurs et autres tiers comptables")
@RestController
@RequestMapping("/api/v1/tiers")
@RequiredArgsConstructor
public class TiersController {

    private final TiersUseCase tiersUseCase;

    @Operation(summary = "Lister les tiers", description = "Tiers d'une filiale avec pagination. Filtrables par type : CLIENT, FOURNISSEUR, EMPLOYE, ADMINISTRATION")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Tiers>>> lister(
            @RequestParam UUID filialeId,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(tiersUseCase.listerParFiliale(filialeId, type, pageable))));
    }

    @Operation(summary = "Obtenir un tiers", description = "Retourne les détails d'un tiers")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tiers>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(tiersUseCase.trouverParId(id)));
    }

    @Operation(summary = "Créer un tiers", description = "Enregistre un nouveau tiers pour la filiale")
    @PostMapping
    public ResponseEntity<ApiResponse<Tiers>> creer(@Valid @RequestBody TiersRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(tiersUseCase.creer(request)));
    }

    @Operation(summary = "Modifier un tiers", description = "Met à jour les informations d'un tiers")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Tiers>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody TiersRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(tiersUseCase.modifier(id, request)));
    }

    @Operation(summary = "Supprimer un tiers", description = "Supprime un tiers (uniquement si aucune écriture rattachée)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(@PathVariable UUID id) {
        tiersUseCase.supprimer(id);
        return ResponseEntity.ok(ApiResponse.ok("Tiers supprimé"));
    }
}
