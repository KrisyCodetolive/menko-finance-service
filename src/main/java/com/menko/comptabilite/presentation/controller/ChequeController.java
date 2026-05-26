package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.ChequeRequest;
import com.menko.comptabilite.application.dto.request.StatutChequeRequest;
import com.menko.comptabilite.application.port.in.ChequeUseCase;
import com.menko.comptabilite.domain.model.Cheque;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cheques")
@RequiredArgsConstructor
public class ChequeController {

    private final ChequeUseCase chequeUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Cheque>>> lister(
            @RequestParam UUID compteBancaireId,
            @RequestParam(required = false) String statut,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(chequeUseCase.listerParCompteBancaire(compteBancaireId, statut, pageable))));
    }

    @GetMapping("/alertes")
    public ResponseEntity<ApiResponse<List<Cheque>>> alertes(
            @RequestParam UUID filialeId,
            @RequestParam(defaultValue = "30") int joursDepuis) {
        return ResponseEntity.ok(ApiResponse.ok(
                chequeUseCase.alertesChequesNonEncaisses(filialeId, joursDepuis)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cheque>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(chequeUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cheque>> emettre(@Valid @RequestBody ChequeRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(chequeUseCase.emettre(request)));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<ApiResponse<Cheque>> mettreAJourStatut(
            @PathVariable UUID id,
            @Valid @RequestBody StatutChequeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(chequeUseCase.mettreAJourStatut(id, request)));
    }
}
