package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.EcritureComptableRequest;
import com.menko.comptabilite.application.port.in.EcritureComptableUseCase;
import com.menko.comptabilite.domain.model.EcritureComptable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Écritures comptables", description = "Saisie, validation et clôture des écritures comptables SYSCOHADA")
@RestController
@RequestMapping("/api/v1/ecritures")
@RequiredArgsConstructor
public class EcritureComptableController {

    private final EcritureComptableUseCase ecritureUseCase;

    @Operation(summary = "Lister les écritures", description = "Écritures d'une filiale avec pagination et filtres de dates optionnels")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EcritureComptable>>> lister(
            @RequestParam UUID filialeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(ecritureUseCase.listerParFiliale(filialeId, debut, fin, pageable))));
    }

    @Operation(summary = "Obtenir une écriture", description = "Retourne une écriture comptable avec ses lignes de détail")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EcritureComptable>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(ecritureUseCase.trouverParId(id)));
    }

    @Operation(summary = "Saisir une écriture", description = "Crée une écriture en statut BROUILLON. Les lignes doivent être équilibrées (Σdébit = Σcrédit)")
    @PostMapping
    public ResponseEntity<ApiResponse<EcritureComptable>> creer(
            @Valid @RequestBody EcritureComptableRequest request) {
        UUID utilisateurId = UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.status(201).body(
                ApiResponse.created(ecritureUseCase.creer(request, utilisateurId)));
    }

    @Operation(summary = "Modifier une écriture", description = "Modifie une écriture. Autorisé uniquement si statut = BROUILLON")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EcritureComptable>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody EcritureComptableRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(ecritureUseCase.modifier(id, request)));
    }

    @Operation(summary = "Supprimer une écriture", description = "Supprime une écriture. Autorisé uniquement si statut = BROUILLON")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(@PathVariable UUID id) {
        ecritureUseCase.supprimer(id);
        return ResponseEntity.ok(ApiResponse.ok("Écriture supprimée"));
    }

    @Operation(summary = "Valider une écriture", description = "BROUILLON → VALIDEE. L'écriture ne peut plus être modifiée ni supprimée après validation")
    @PostMapping("/{id}/valider")
    public ResponseEntity<ApiResponse<EcritureComptable>> valider(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(ecritureUseCase.valider(id)));
    }

    @Operation(summary = "Clôturer une écriture", description = "VALIDEE → CLOTUREE. L'écriture est incluse dans la balance et le grand livre")
    @PostMapping("/{id}/cloturer")
    public ResponseEntity<ApiResponse<EcritureComptable>> cloturer(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(ecritureUseCase.cloturer(id)));
    }
}
