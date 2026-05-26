package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.JournalComptableRequest;
import com.menko.comptabilite.application.port.in.JournalComptableUseCase;
import com.menko.comptabilite.domain.model.JournalComptable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Journaux comptables", description = "Plan des journaux comptables (VTE, ACH, SAL, OI, BQ…)")
@RestController
@RequestMapping("/api/v1/journaux")
@RequiredArgsConstructor
public class JournalComptableController {

    private final JournalComptableUseCase journalUseCase;

    @Operation(summary = "Lister les journaux", description = "Journaux comptables d'une filiale")
    @GetMapping
    public ResponseEntity<ApiResponse<List<JournalComptable>>> lister(@RequestParam UUID filialeId) {
        return ResponseEntity.ok(ApiResponse.ok(journalUseCase.listerParFiliale(filialeId)));
    }

    @Operation(summary = "Obtenir un journal", description = "Retourne un journal comptable par son identifiant")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalComptable>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(journalUseCase.trouverParId(id)));
    }

    @Operation(summary = "Créer un journal", description = "Ajoute un journal au plan des journaux de la filiale")
    @PostMapping
    public ResponseEntity<ApiResponse<JournalComptable>> creer(
            @Valid @RequestBody JournalComptableRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(journalUseCase.creer(request)));
    }

    @Operation(summary = "Modifier un journal", description = "Met à jour le libellé ou le code d'un journal")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalComptable>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody JournalComptableRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(journalUseCase.modifier(id, request)));
    }

    @Operation(summary = "Supprimer un journal", description = "Supprime un journal comptable (uniquement si aucune écriture rattachée)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(@PathVariable UUID id) {
        journalUseCase.supprimer(id);
        return ResponseEntity.ok(ApiResponse.ok("Journal supprimé"));
    }
}
