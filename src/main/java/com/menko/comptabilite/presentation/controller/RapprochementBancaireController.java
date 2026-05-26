package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.LigneRapprochementRequest;
import com.menko.comptabilite.application.dto.request.RapprochementBancaireRequest;
import com.menko.comptabilite.application.dto.request.StatutLigneRapprochementRequest;
import com.menko.comptabilite.application.port.in.RapprochementBancaireUseCase;
import com.menko.comptabilite.domain.model.LigneRapprochement;
import com.menko.comptabilite.domain.model.RapprochementBancaire;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rapprochements")
@RequiredArgsConstructor
public class RapprochementBancaireController {

    private final RapprochementBancaireUseCase rapprochementUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RapprochementBancaire>>> lister(
            @RequestParam UUID compteBancaireId) {
        return ResponseEntity.ok(ApiResponse.ok(
                rapprochementUseCase.listerParCompteBancaire(compteBancaireId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RapprochementBancaire>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(rapprochementUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RapprochementBancaire>> ouvrirSession(
            @Valid @RequestBody RapprochementBancaireRequest request) {
        UUID utilisateurId = UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.status(201).body(
                ApiResponse.created(rapprochementUseCase.ouvrirSession(request, utilisateurId)));
    }

    @PostMapping("/{id}/terminer")
    public ResponseEntity<ApiResponse<Void>> terminerSession(@PathVariable UUID id) {
        rapprochementUseCase.terminerSession(id);
        return ResponseEntity.ok(ApiResponse.ok("Session de rapprochement terminée"));
    }

    @GetMapping("/{id}/lignes")
    public ResponseEntity<ApiResponse<List<LigneRapprochement>>> listerLignes(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(rapprochementUseCase.listerLignes(id)));
    }

    @PostMapping("/{id}/lignes")
    public ResponseEntity<ApiResponse<LigneRapprochement>> ajouterLigne(
            @PathVariable UUID id,
            @Valid @RequestBody LigneRapprochementRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.created(rapprochementUseCase.ajouterLigne(id, request)));
    }

    @PatchMapping("/lignes/{ligneId}/valider")
    public ResponseEntity<ApiResponse<LigneRapprochement>> validerLigne(
            @PathVariable UUID ligneId,
            @Valid @RequestBody StatutLigneRapprochementRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(rapprochementUseCase.validerLigne(ligneId, request)));
    }
}
