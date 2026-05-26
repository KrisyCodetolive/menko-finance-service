package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.LigneRapprochementRequest;
import com.menko.comptabilite.application.dto.request.RapprochementBancaireRequest;
import com.menko.comptabilite.application.dto.request.StatutLigneRapprochementRequest;
import com.menko.comptabilite.application.port.in.RapprochementBancaireUseCase;
import com.menko.comptabilite.domain.model.LigneRapprochement;
import com.menko.comptabilite.domain.model.RapprochementBancaire;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Rapprochement bancaire", description = "Rapprochement entre les mouvements bancaires et les écritures comptables")
@RestController
@RequestMapping("/api/v1/rapprochements")
@RequiredArgsConstructor
public class RapprochementBancaireController {

    private final RapprochementBancaireUseCase rapprochementUseCase;

    @Operation(summary = "Lister les sessions", description = "Sessions de rapprochement d'un compte bancaire")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RapprochementBancaire>>> lister(
            @RequestParam UUID compteBancaireId) {
        return ResponseEntity.ok(ApiResponse.ok(
                rapprochementUseCase.listerParCompteBancaire(compteBancaireId)));
    }

    @Operation(summary = "Obtenir une session", description = "Retourne les détails d'une session de rapprochement")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RapprochementBancaire>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(rapprochementUseCase.trouverParId(id)));
    }

    @Operation(summary = "Ouvrir une session", description = "Démarre une nouvelle session de rapprochement bancaire")
    @PostMapping
    public ResponseEntity<ApiResponse<RapprochementBancaire>> ouvrirSession(
            @Valid @RequestBody RapprochementBancaireRequest request) {
        UUID utilisateurId = UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.status(201).body(
                ApiResponse.created(rapprochementUseCase.ouvrirSession(request, utilisateurId)));
    }

    @Operation(summary = "Terminer une session", description = "Clôture manuellement une session de rapprochement")
    @PostMapping("/{id}/terminer")
    public ResponseEntity<ApiResponse<Void>> terminerSession(@PathVariable UUID id) {
        rapprochementUseCase.terminerSession(id);
        return ResponseEntity.ok(ApiResponse.ok("Session de rapprochement terminée"));
    }

    @Operation(summary = "Lister les lignes", description = "Lignes de rapprochement d'une session")
    @GetMapping("/{id}/lignes")
    public ResponseEntity<ApiResponse<List<LigneRapprochement>>> listerLignes(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(rapprochementUseCase.listerLignes(id)));
    }

    @Operation(summary = "Ajouter une ligne", description = "Ajoute un mouvement bancaire à la session de rapprochement")
    @PostMapping("/{id}/lignes")
    public ResponseEntity<ApiResponse<LigneRapprochement>> ajouterLigne(
            @PathVariable UUID id,
            @Valid @RequestBody LigneRapprochementRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.created(rapprochementUseCase.ajouterLigne(id, request)));
    }

    @Operation(summary = "Valider une ligne", description = "Valide ou rejette une ligne. Si toutes les lignes sont validées, la session se clôture automatiquement")
    @PatchMapping("/lignes/{ligneId}/valider")
    public ResponseEntity<ApiResponse<LigneRapprochement>> validerLigne(
            @PathVariable UUID ligneId,
            @Valid @RequestBody StatutLigneRapprochementRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(rapprochementUseCase.validerLigne(ligneId, request)));
    }
}
