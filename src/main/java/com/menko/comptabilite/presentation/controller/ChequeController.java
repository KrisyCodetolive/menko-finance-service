package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.ChequeRequest;
import com.menko.comptabilite.application.dto.request.StatutChequeRequest;
import com.menko.comptabilite.application.port.in.ChequeUseCase;
import com.menko.comptabilite.domain.model.Cheque;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Chèques", description = "Émission, suivi et alertes sur les chèques bancaires")
@RestController
@RequestMapping("/api/v1/cheques")
@RequiredArgsConstructor
public class ChequeController {

    private final ChequeUseCase chequeUseCase;

    @Operation(summary = "Lister les chèques", description = "Chèques d'un compte bancaire avec pagination. Filtrables par statut : EMIS, ENCAISSE, REJETE, ANNULE")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Cheque>>> lister(
            @RequestParam UUID compteBancaireId,
            @RequestParam(required = false) String statut,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(chequeUseCase.listerParCompteBancaire(compteBancaireId, statut, pageable))));
    }

    @Operation(summary = "Alertes chèques non encaissés", description = "Chèques émis non encaissés depuis N jours (défaut 30). Utilisé pour la gestion des impayés et relances")
    @GetMapping("/alertes")
    public ResponseEntity<ApiResponse<List<Cheque>>> alertes(
            @RequestParam UUID filialeId,
            @RequestParam(defaultValue = "30") int joursDepuis) {
        return ResponseEntity.ok(ApiResponse.ok(
                chequeUseCase.alertesChequesNonEncaisses(filialeId, joursDepuis)));
    }

    @Operation(summary = "Obtenir un chèque", description = "Retourne les détails d'un chèque par son identifiant")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cheque>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(chequeUseCase.trouverParId(id)));
    }

    @Operation(summary = "Émettre un chèque", description = "Enregistre l'émission d'un chèque en statut EMIS et génère l'écriture comptable associée")
    @PostMapping
    public ResponseEntity<ApiResponse<Cheque>> emettre(@Valid @RequestBody ChequeRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(chequeUseCase.emettre(request)));
    }

    @Operation(summary = "Mettre à jour le statut", description = "Transitions autorisées : EMIS → ENCAISSE, EMIS → REJETE, EMIS → ANNULE")
    @PatchMapping("/{id}/statut")
    public ResponseEntity<ApiResponse<Cheque>> mettreAJourStatut(
            @PathVariable UUID id,
            @Valid @RequestBody StatutChequeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(chequeUseCase.mettreAJourStatut(id, request)));
    }
}
