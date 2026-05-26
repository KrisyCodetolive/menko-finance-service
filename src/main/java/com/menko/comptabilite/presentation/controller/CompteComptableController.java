package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.CompteComptableRequest;
import com.menko.comptabilite.application.port.in.CompteComptableUseCase;
import com.menko.comptabilite.domain.model.CompteComptable;
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

@Tag(name = "Plan comptable", description = "Plan comptable SYSCOHADA des filiales")
@RestController
@RequestMapping("/api/v1/comptes-comptables")
@RequiredArgsConstructor
public class CompteComptableController {

    private final CompteComptableUseCase compteUseCase;

    @Operation(summary = "Lister les comptes", description = "Plan comptable d'une filiale avec pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CompteComptable>>> lister(
            @RequestParam UUID filialeId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(compteUseCase.listerParFiliale(filialeId, pageable))));
    }

    @Operation(summary = "Lister par classe", description = "Comptes d'une filiale filtrés par classe SYSCOHADA (1=Capitaux, 2=Immobilisations, 3=Stocks, 4=Tiers, 5=Trésorerie, 6=Charges, 7=Produits)")
    @GetMapping("/classe/{classe}")
    public ResponseEntity<ApiResponse<List<CompteComptable>>> listerParClasse(
            @RequestParam UUID filialeId,
            @PathVariable Integer classe) {
        return ResponseEntity.ok(ApiResponse.ok(
                compteUseCase.listerParFilialeEtClasse(filialeId, classe)));
    }

    @Operation(summary = "Obtenir un compte", description = "Retourne un compte comptable par son identifiant")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompteComptable>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(compteUseCase.trouverParId(id)));
    }

    @Operation(summary = "Créer un compte", description = "Ajoute un compte au plan comptable de la filiale")
    @PostMapping
    public ResponseEntity<ApiResponse<CompteComptable>> creer(
            @Valid @RequestBody CompteComptableRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(compteUseCase.creer(request)));
    }

    @Operation(summary = "Modifier un compte", description = "Met à jour le libellé ou les propriétés d'un compte")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompteComptable>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody CompteComptableRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(compteUseCase.modifier(id, request)));
    }

    @Operation(summary = "Archiver un compte", description = "Archive un compte comptable (pas de suppression physique)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> archiver(@PathVariable UUID id) {
        compteUseCase.archiver(id);
        return ResponseEntity.ok(ApiResponse.ok("Compte archivé"));
    }
}
